package wineshop.order;
import com.google.common.collect.Lists;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.*;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import wineshop.customer.Customer;
import wineshop.customer.CustomerManager;
import wineshop.wine.CatalogManager;
import wineshop.wine.Wine;

import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;

import javax.validation.Valid;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.Math.round;


@Controller
@RequestMapping("/order")
@SessionAttributes("cart")
public class OrderCustController {
	private final OrderManagement<OrderCust> orderManagement;
	//private final OrderManagement<Preorder> preorderManagement;
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final CatalogManager catalogManager;
	private final CustomerManager customerManager;
	private final OrderCustManager orderCustManager;


	public OrderCustController(OrderManagement<OrderCust> orderManagement,/*OrderManagement<Preorder> preorderManagement,*/ UniqueInventory<UniqueInventoryItem> inventory, CatalogManager catalogManager, CustomerManager customerManager, OrderCustManager orderCustManager) {
		this.orderManagement = orderManagement;
		//this.preorderManagement = preorderManagement;
		this.inventory = inventory;
		this.catalogManager=catalogManager;
		this.customerManager = customerManager;
		this.orderCustManager = orderCustManager;
	}


	// for session attribute cart
	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@GetMapping("/addToCart/{id}/{quantity}")
	public String addToCart(@PathVariable ProductIdentifier id, @PathVariable int quantity, @ModelAttribute("cart")  Cart cart) {
		Wine wine = catalogManager.findById(id);
		cart.addOrUpdateItem(wine, Quantity.of(quantity));
		return "redirect:/catalog";}

	// shopping cart is shown on website
	@GetMapping("/cart")
	String cart(Model model, CartCustForm cartCustForm){
		model.addAttribute("customers", customerManager.findAll());
		model.addAttribute("cartCustForm", cartCustForm);
		model.addAttribute("stock", inventory);
		return "order/cart";
	}

	// updates the Quantity of given CartItem through its id when pressing the refresh button
	@PostMapping("cart/refresh")
	String refresh(@RequestParam("iid") String itemId, @RequestParam("number") int number, @ModelAttribute Cart cart) {
		orderCustManager.refresh(itemId, number, cart);

		return "redirect:/order/cart";
	}

	// deletes the given CartItem through its id when pressing the delete button
	@PostMapping("cart/deleteItem/{itemId}")
	String deleteItem(@PathVariable String itemId, @ModelAttribute Cart cart) {
		orderCustManager.deleteItem(itemId, cart);
System.out.println("Ich bin hier" + itemId);
		return  "redirect:/order/cart";
	}


	// is called when someone is inside shopping cart and presses 'buy'. you get redirected to index page.
	@PostMapping("/checkout")
	String buy(@LoggedIn UserAccount userAccount, @ModelAttribute Cart cart, @Valid CartCustForm cartCustForm, Errors result) {
		if (result.hasErrors()) return "failure";
		orderCustManager.cartToOrderAndPreOrder(userAccount, cart, cartCustForm);
		cart.clear();
		return "redirect:/catalog";
	}

	@GetMapping("/orders")
	String orders(Model model, @RequestParam("search") Optional<String> search, @RequestParam Optional<String> monat) {

		List<OrderCust> orders = orderManagement.findBy(OrderStatus.COMPLETED).toList();
		List<OrderCust> filtered_orders = orders;

		if (search.isPresent()){
			filtered_orders = orders.stream().filter((e) -> {
				if(e.isOrder()) {
					return  e.getCustomer().getFamilyName().toLowerCase().contains(search.get().toLowerCase());
				} else {
					return  e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
				}
			}).collect(Collectors.toList());
			if(filtered_orders.isEmpty()) {
				filtered_orders = orders.stream().filter((e) -> {
					if(e.isOrder()) {
						return e.getCustomer().getFirstName().toLowerCase().contains(search.get().toLowerCase());
					}else {
						return  e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
					}
				}).collect(Collectors.toList());
			}
		}

		if (monat.isPresent()){
				filtered_orders = orders.stream().filter((e) -> {
					return  e.getDateCreated().getMonth().toString().contains(monat.get());
				}).collect(Collectors.toList());
		}

		model.addAttribute("orders", filtered_orders);

		return "order/orders";
	}

	@GetMapping("/detail/{id}")
	String ordersDetail(Model model, @RequestParam("id") OrderIdentifier id) {
		OrderCust order = orderManagement.get(id).get();

		model.addAttribute("order", order);
		model.addAttribute("orderLines", order.getOrderLines().toList());
		model.addAttribute("inventory", inventory);
		return "order/detail";
	}

	@GetMapping("/balancing")
	String balancing(Model model,  @RequestParam("search") Optional<String> search, @RequestParam Optional<String> monat) {
		List<OrderCust> orders = orderManagement.findBy(OrderStatus.COMPLETED).toList();

		List<OrderCust> filtered_orders = orders;

		if (search.isPresent()){
			filtered_orders = orders.stream().filter((e) -> {
				if(e.isOrder()) {
					return  e.getCustomer().getFamilyName().toLowerCase().contains(search.get().toLowerCase());
				} else if(e.isReorder()){
					return  e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
				} else {
					return false;
				}
			}).collect(Collectors.toList());
			if(filtered_orders.isEmpty()) {
				filtered_orders = orders.stream().filter((e) -> {
					if(e.isOrder()) {
						return e.getCustomer().getFirstName().toLowerCase().contains(search.get().toLowerCase());
					}else if(e.isReorder()){
						return  e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
					} else {
						return false;
					}
				}).collect(Collectors.toList());
			}
		}

		if (monat.isPresent()){
			filtered_orders = orders.stream().filter((e) -> {
				return  e.getDateCreated().getMonth().toString().contains(monat.get());
			}).collect(Collectors.toList());
		}

		filtered_orders = Lists.reverse(filtered_orders.stream().sorted(Comparator.comparing(OrderCust::getDateCreated)).collect(Collectors.toList()));

		double totalPrice = 0;

		for(int i = 0; i < filtered_orders.size(); i++) {
			totalPrice += filtered_orders.get(i).getTotal().getNumber().doubleValue();
		}

		totalPrice *= 100;
		totalPrice = round(totalPrice);
		totalPrice /= 100;

		model.addAttribute("orders", filtered_orders);
		model.addAttribute("totalPrice", totalPrice);

		return "order/balancing";
	}

	@GetMapping("/empty")
	String emptyBasket(@ModelAttribute Cart cart){
		cart.clear();
		return "redirect:/order/cart";
	}
}
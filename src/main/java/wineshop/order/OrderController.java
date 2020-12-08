package wineshop.order;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.*;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import wineshop.wine.CatalogManager;
import wineshop.wine.Wine;

import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;

import javax.lang.model.element.QualifiedNameable;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import java.util.Iterator;
import java.util.List;

import static org.salespointframework.core.Currencies.EURO;


@Controller
@RequestMapping("/order")
@SessionAttributes("cart")
public class OrderController {

	private final OrderManagement<Order> orderManagement;
	//private final OrderManagement<Preorder> preorderManagement;
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final CatalogManager catalogManager;

	public OrderController(OrderManagement<Order> orderManagement,/*OrderManagement<Preorder> preorderManagement,*/ UniqueInventory<UniqueInventoryItem> inventory, CatalogManager catalogManager) {
		this.orderManagement = orderManagement;
		//this.preorderManagement = preorderManagement;
		this.inventory = inventory;
		this.catalogManager=catalogManager;
	}


	// for session attribute cart
	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}


	@GetMapping("/addToCart/{id}/{quantity}")
	public String addToCart(@PathVariable ProductIdentifier id, @PathVariable int quantity, @ModelAttribute Cart cart) {
		Wine wine = catalogManager.findById(id);
		cart.addOrUpdateItem(wine, Quantity.of(quantity));
		//cart.addOrUpdate(wine, Quantity.of(quantity), customer)
		return "redirect:/catalog";}



	// shopping cart is shown on website
	@GetMapping("/cart")
	String cart(){
		return "order/cart";
	}


	// is called when someone is inside shopping cart and presses 'buy'. you get redirected to index page.
	@PostMapping("/checkout")
	String buy(@ModelAttribute Cart cart, @LoggedIn UserAccount userAccount) {
		// Mit completeOrder(…) wird der Warenkorb in die Order überführt, diese wird dann bezahlt und abgeschlossen.
		// Orders können nur abgeschlossen werden, wenn diese vorher bezahlt wurden.
		var order = new Order(userAccount, Cash.CASH);
		//TODO: NEU
		var preorder = new Order(userAccount, Cash.CASH);
		//TODO: ---
		//cart.addItemsTo(order);
		//TODO: NEU
		// 		Nachbestellungen: 	die Quantity von CartItem und IventoryItem wird verglichen,
		//							ist die Quantity kleiner, wird das CartItem in die Preorder
		//							überführt, ansonsten in die Order
		Iterator<CartItem> item = cart.iterator();
		do {
			CartItem cartItem = item.next();
			Wine wine = (Wine) cartItem.getProduct();
			InventoryItem inventoryItem = inventory.findByProductIdentifier(wine.getId()).get();

			if (cartItem.getQuantity().isGreaterThan(inventoryItem.getQuantity())) {
				preorder.addOrderLine(wine, cartItem.getQuantity());
				//TODO: Hier findet das "Nachbestellen" statt
				inventoryItem.increaseQuantity((Quantity.of(10).subtract(inventoryItem.getQuantity())).add(cartItem.getQuantity()));
				//preorder.reorder(inventoryItem);
			} else {
				order.addOrderLine(wine, cartItem.getQuantity());;
			}
		}
		while (item.hasNext());
		//TODO:	Falls eine Order leer sein sollte, wird diese gelöscht.
		//		Ansonsten wird sie bezahlt und anschließend geschlossen.
		if (preorder.getOrderLines().get().count() == 0) {
			orderManagement.delete(preorder);
		} else {
			orderManagement.payOrder(preorder);
			orderManagement.completeOrder(preorder);
		}
		if (order.getOrderLines().get().count() == 0) {
			orderManagement.delete(order);
		} else {
			orderManagement.payOrder(order);
			orderManagement.completeOrder(order);
		}
		//TODO: ---
		cart.clear();
		return "redirect:/";
	}


	@GetMapping("/orders")
	String orders(Model model) {
		model.addAttribute("orders", orderManagement.findBy(OrderStatus.COMPLETED));

		return "/order/orders";
	}


	@GetMapping("/detail/{id}")
	String ordersDetail(Model model, @RequestParam("id") OrderIdentifier id) {
		Order order = orderManagement.get(id).get();

		model.addAttribute("order", order);
		model.addAttribute("orderLines", order.getOrderLines().toList());

		return "/order/detail";
	}

	@GetMapping("/balancing")
	String balancing(Model model) {
		Streamable<Order> orders = orderManagement.findBy(OrderStatus.COMPLETED);
		List<Order> list = orders.toList();

		double totalPrice = 0;

		for(int i = 0; i < list.size(); i++) {
			totalPrice += list.get(i).getTotal().getNumber().doubleValue();
		}

		model.addAttribute("orders", orders);
		model.addAttribute("totalPrice", totalPrice);

		return "/order/balancing";
	}
}
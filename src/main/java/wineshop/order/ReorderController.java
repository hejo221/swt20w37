package wineshop.order;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wineshop.wine.Wine;
import wineshop.inventory.InventoryManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ReorderController {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final OrderManagement<OrderCust> orderManagement;
	private final ReorderManager reorderManager;

	private static final Logger LOG = LoggerFactory.getLogger(ReorderController.class);

	ReorderController(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager, OrderManagement<OrderCust> orderManagement, ReorderManager reorderManager) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
		this.orderManagement = orderManagement;
		this.reorderManager = reorderManager;
	}

	@GetMapping("/reorders")
	String reorders(Model model, @RequestParam("search") Optional<String> search) {
		List<OrderCust> reorders = orderManagement.findBy(OrderStatus.OPEN).toList();
		List<OrderCust> filtered_reorders = reorders;

		if (search.isPresent()){
			filtered_reorders = reorders.stream().filter((e) -> {
				if(e.isOrder()) {
					return  e.getCustomer().getFamilyName().toLowerCase().contains(search.get().toLowerCase());
				} else {
					return  e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
				}
			}).collect(Collectors.toList());
			if(filtered_reorders.isEmpty()) {
				filtered_reorders = reorders.stream().filter((e) -> {
					if(e.isOrder()) {
						return e.getCustomer().getFirstName().toLowerCase().contains(search.get().toLowerCase());
					}else {
						return  e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
					}
				}).collect(Collectors.toList());
			}
		}

		model.addAttribute("reorders", filtered_reorders);

		return "order/reorders";
	}

	@PostMapping("/reorders/{productId}")
	String reorderWine(@PathVariable ProductIdentifier productId, @RequestParam("number") int amount, @LoggedIn UserAccount userAccount) {
		reorderManager.reorderWine(productId, amount, userAccount);

		return "redirect:/inventory";
	}

	@GetMapping("/reorders/detail/{id}")
	String ordersDetail(Model model, @RequestParam("id") OrderIdentifier id) {
		OrderCust reorder = orderManagement.get(id).get();

		model.addAttribute("order", reorder);
		model.addAttribute("orderLines", reorder.getOrderLines().toList());
		model.addAttribute("inventory", inventory);

		return "order/detail";
	}

	@PostMapping("reorders/close")
	String closeReorder(@RequestParam("id") OrderIdentifier id) {
		reorderManager.closeReorder(id);

		return "redirect:/reorders";
	}

	/*
	@PostMapping("/reorders/createAndClose/{productId}")
	String createAndClose(@LoggedIn UserAccount userAccount, @PathVariable ProductIdentifier productId) {
		Wine wine = (Wine) inventory.findByProductIdentifier(productId).get().getProduct();
		var reorder = new OrderCust(userAccount, Cash.CASH, OrderType.ORDER);
		reorder.setOrderType(OrderType.REORDER);
		reorder.addOrderLine(wine, Quantity.of(10));
		orderManagement.save(reorder);
		orderManagement.payOrder(reorder);
		orderManagement.completeOrder(reorder);

		LOG.info(String.valueOf(reorder.getOrderType()));

		return "redirect:/inventory";
	}
	*/
}

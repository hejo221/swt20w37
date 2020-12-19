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
import wineshop.zinventory.InventoryManager;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class PreorderController {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final OrderManagement<OrderCust> orderManagement;

	private static final Logger LOG = LoggerFactory.getLogger(ReorderController.class);

	PreorderController(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager, OrderManagement<OrderCust> orderManagement) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
		this.orderManagement = orderManagement;
	}

	@GetMapping("/preorders")
	String preorders(Model model) {
		model.addAttribute("preorders", orderManagement.findBy(OrderStatus.OPEN));
		model.addAttribute("inventory", inventory);

		return "/order/preorders";
	}

	@GetMapping("/preorders/detail/{id}")
	String preorderDetail(Model model, @RequestParam("id") OrderIdentifier id) {
		OrderCust preorder = orderManagement.get(id).get();

		model.addAttribute("order", preorder);
		model.addAttribute("orderLines", preorder.getOrderLines().toList());
		model.addAttribute("inventory", inventory);

		return "/order/detail";
	}

	@PostMapping("preorders/close")
	String closePreorder(@RequestParam("id") OrderIdentifier id) {
		OrderCust preorder = orderManagement.get(id).get();

		orderManagement.payOrder(preorder);
		orderManagement.completeOrder(preorder);

		return "redirect:/reorders";
	}
}

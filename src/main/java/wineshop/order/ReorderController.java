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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Ein Spring MVC Controller, welcher für die Veraltung von Nachbestellungen zuständig ist
 */
@Controller
public class ReorderController {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final OrderManagement<OrderCust> orderManagement;
	private final ReorderManager reorderManager;

	private static final Logger LOG = LoggerFactory.getLogger(ReorderController.class);

	private int email_flag = 0;

	ReorderController(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager, OrderManagement<OrderCust> orderManagement, ReorderManager reorderManager) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
		this.orderManagement = orderManagement;
		this.reorderManager = reorderManager;
	}

	/**
	 * Die Nachbestellungen werden angezeigt
	 *
	 * @param model
	 * @param search
	 * @return View-Name
	 */
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
		model.addAttribute("email_flag", email_flag);
		email_flag = 0;

		return "order/reorders";
	}

	/**
	 * Ein Wein wird nachbestellt
	 *
	 * @param productId Die Id des Weines
	 * @param amount Die gewünschte Anzahl (veraltet)
	 * @param userAccount Der angemeldete Mitarbeiter
	 * @return View-Name
	 */
	@PostMapping("/reorders/{productId}")
	String reorderWine(@PathVariable ProductIdentifier productId, @RequestParam("number") int amount, @LoggedIn UserAccount userAccount) {
		reorderManager.reorderWine(productId, amount, userAccount);

		return "redirect:/inventory";
	}

	/**
	 * Die Detailansicht einer Nachbestellung wird angezeigt
	 *
	 * @param model
	 * @param id Die Id der Nachbestellung
	 * @return View-Name
	 */
	@GetMapping("/reorders/detail/{id}")
	String ordersDetail(Model model, @RequestParam("id") OrderIdentifier id) {
		OrderCust reorder = orderManagement.get(id).get();

		model.addAttribute("order", reorder);
		model.addAttribute("orderLines", reorder.getOrderLines().toList());
		model.addAttribute("inventory", inventory);

		return "order/detail";
	}

	/**
	 * Die Nachbestellung wird geschlossen
	 *
	 * @param id Die Id der Nachbestellung
	 * @return View-Name
	 */
	@PostMapping("reorders/close")
	String closeReorder(@RequestParam("id") OrderIdentifier id) {
		email_flag = reorderManager.closeReorder(id);

		return "redirect:/reorders";
	}

	/**
	 * Die Nachbestellung wird gelöscht
	 *
	 * @param id Die Id der Nachbestellung
	 * @return View-Name
	 */
	@PostMapping("reorders/delete")
	public String deleteReorder(@RequestParam("id") OrderIdentifier id) {
		OrderCust reorder = orderManagement.get(id).get();
		orderManagement.delete(reorder);
		return "redirect:/reorders";
	}
}

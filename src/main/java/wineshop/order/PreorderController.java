package wineshop.order;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wineshop.inventory.InventoryManager;
import wineshop.wine.Wine;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PreorderController {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final OrderManagement<OrderCust> orderManagement;
	private final PreorderManager preorderManager;
	private final ReorderManager reorderManager;

	private static final Logger LOG = LoggerFactory.getLogger(ReorderController.class);

	PreorderController(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager, OrderManagement<OrderCust> orderManagement,PreorderManager preorderManager,ReorderManager reorderManager) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
		this.orderManagement = orderManagement;
		this.preorderManager = preorderManager;
		this.reorderManager = reorderManager;
	}

	@GetMapping("/preorders")
	public String preorders(Model model, @RequestParam("search") Optional<String> search) {
		List<OrderCust> preorders = orderManagement.findBy(OrderStatus.OPEN).toList();
		List<OrderCust> filtered_preorders = preorders;

		if (search.isPresent()){
			filtered_preorders = preorders.stream().filter((e) -> {
				if(e.isPreorder()) {
					return  e.getCustomer().getFamilyName().toLowerCase().contains(search.get().toLowerCase());
				} else {
					return  e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
				}
			}).collect(Collectors.toList());
			if(filtered_preorders.isEmpty()) {
				filtered_preorders = preorders.stream().filter((e) -> {
					if(e.isPreorder()) {
						return e.getCustomer().getFirstName().toLowerCase().contains(search.get().toLowerCase());
					}else {
						return  e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
					}
				}).collect(Collectors.toList());
			}
		}

		model.addAttribute("preorders", filtered_preorders);
		model.addAttribute("inventory", inventory);

		return "order/preorders";
	}

	@GetMapping("/preorders/detail/{id}")
	public String preorderDetail(Model model, @RequestParam("id") OrderIdentifier id) {
		OrderCust preorder = orderManagement.get(id).get();

		model.addAttribute("order", preorder);
		model.addAttribute("orderLines", preorder.getOrderLines().toList());
		model.addAttribute("inventory", inventory);

		return "order/detail";
	}

	@PostMapping("preorders/close")
	public String closePreorder(@RequestParam("id") OrderIdentifier id) {
		OrderCust preorder = orderManagement.get(id).get();
		Iterator<OrderLine> preorderIterator = preorder.getOrderLines().iterator();
		do {
			OrderLine orderLine = preorderIterator.next();
			ProductIdentifier productId = orderLine.getProductIdentifier();
			Quantity orderLineQuantity = orderLine.getQuantity();
			Quantity inventoryQuantity = inventory.findByProductIdentifier(productId).get().getQuantity();
			Wine wine = (Wine) inventory.findByProductIdentifier(productId).get().getProduct();
			if (inventoryQuantity.subtract(orderLineQuantity).isLessThan(wine.getMinAmount())) {
				reorderManager.reorderWine(wine.getId(), Quantity.of(30).subtract(inventoryQuantity.subtract(orderLineQuantity)).getAmount().intValue(), preorder.getUserAccount());
			}
		} while (preorderIterator.hasNext());
		orderManagement.payOrder(preorder);
		orderManagement.completeOrder(preorder);

		return "redirect:/preorders";
	}

	@Transactional
	@PostMapping("preorders/delete")
	public String deletePreorder(@RequestParam("id") OrderIdentifier id) {
		OrderCust preorder = orderManagement.get(id).get();
		orderManagement.delete(preorder);
		return "redirect:/preorders";
	}
}

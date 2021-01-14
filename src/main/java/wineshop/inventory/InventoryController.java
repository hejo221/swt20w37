package wineshop.inventory;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
class InventoryController {


	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private static final Logger LOG = LoggerFactory.getLogger(InventoryInitializer.class);

	InventoryController(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
	}

	@GetMapping("/inventory")
	String stock(Model model) {

		model.addAttribute("stock", inventory.findAll());

		return "inventory/inventory";
	}

	@PostMapping("/inventory/{productId}")
	String updateAmount(@PathVariable ProductIdentifier productId, @RequestParam("number") int number) {
		inventoryManager.updateAmount(productId, number);
		return "redirect:/inventory";
	}

	@PostMapping("/inventory/delete/{productId}")
	String deleteItem(@PathVariable ProductIdentifier productId) {
		inventoryManager.deleteItem((productId));

		return  "redirect:/inventory";
	}

	@PostMapping("/inventory/updateMinAmount/{productId}")
		String updateMinAmount(@PathVariable ProductIdentifier productId, @RequestParam("number") int number) {
		inventoryManager.updateMinAmount(productId, number);

		return  "redirect:/inventory";
	}
}
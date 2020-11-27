package wineshop.zinventory;

import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class InventoryController {

	private final UniqueInventory<UniqueInventoryItem> inventory;

	InventoryController(UniqueInventory<UniqueInventoryItem> inventory) {
		this.inventory = inventory;
	}

	@GetMapping("/inventory")
	String stock(Model model) {

		model.addAttribute("stock", inventory.findAll());

		return "/inventory/inventory";
	}
}

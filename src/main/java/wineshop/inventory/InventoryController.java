package wineshop.inventory;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Ein Spring MVC Controller, welcher für die Lagerverwaltung zuständig ist
 */
@Controller
class InventoryController {


	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private static final Logger LOG = LoggerFactory.getLogger(InventoryInitializer.class);

	InventoryController(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
	}

	/**
	 * Das Lager bzw. inventory wird dargestellt
	 *
	 * @param model
	 * @return View-Name
	 */
	@GetMapping("/inventory")
	String stock(Model model) {

		model.addAttribute("stock", inventory.findAll());

		return "inventory/inventory";
	}

	/**
	 * Der Lagerbestand eines Weines wird aktualisiert
	 *
	 * @param productId Die Id des Weines
	 * @param number der neue Lagerbestand
	 * @return View-Name
	 */
	@PostMapping("/inventory/{productId}")
	String updateAmount(@PathVariable ProductIdentifier productId, @RequestParam("number") int number) {
		inventoryManager.updateAmount(productId, number);
		return "redirect:/inventory";
	}

	/**
	 * Ein Wein wird aus dem Lager entfernt
	 *
	 * @param productId Die Id des Weines
	 * @return	view-Name
	 */
	@PostMapping("/inventory/delete/{productId}")
	String deleteItem(@PathVariable ProductIdentifier productId) {
		inventoryManager.deleteItem((productId));

		return  "redirect:/inventory";
	}

	/**
	 * Der Minimalbestand wird aktualisiert
	 * Fällt der Lagerbestand unter diesen Bestand, können Nachbestellungen automatisch aufgegeben werden
	 *
	 * @param productId Die Id des Weines
	 * @param number Der neue Minimalbestand
	 * @return view-Name
	 */
	@PostMapping("/inventory/updateMinAmount/{productId}")
		String updateMinAmount(@PathVariable ProductIdentifier productId, @RequestParam("number") int number) {
		inventoryManager.updateMinAmount(productId, number);

		return  "redirect:/inventory";
	}

	/**
	 * Der Maximalbestand wird aktualisiert
	 * Der Maximalbestand legt fest, wie viele Weine in einer automatischen Nachbestellung bestellt werden
	 * @param productId Die Id des Weines
	 * @param number Der neue Maximalbestand
	 * @return view-Name
	 */
	@PostMapping("/inventory/updateMaxAmount/{productId}")
	String updateMaxAmount(@PathVariable ProductIdentifier productId, @RequestParam("number") int number) {
		inventoryManager.updateMaxAmount(productId, number);

		return  "redirect:/inventory";
	}
}
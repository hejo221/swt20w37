package wineshop.wine;

import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Optional;


@Controller
class CatalogController {


	private final CatalogManager catalogManager;
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final WineCatalog catalog;

	CatalogController(CatalogManager catalogManager, UniqueInventory<UniqueInventoryItem> inventory, WineCatalog catalog) {
		this.catalogManager = catalogManager;
		this.inventory = inventory;
		this.catalog = catalog;
	}

	@GetMapping("/catalog")
	String showCatalog(Model model, @RequestParam Optional<String> search) {
		if (search.isEmpty()){
			model.addAttribute("wineCatalog", catalogManager.getAllWines());
		}
		else {
			var result = catalog.findAll().filter((e) -> {
				return e.getName().toLowerCase().contains(search.get().toLowerCase());
			}).iterator();
			model.addAttribute("wineCatalog", result);
		}
		return "/wine/catalog";

	}


	@GetMapping("/catalogOfUnavailableWines")
	String showUnavailableWines(Model model) {

		model.addAttribute("wineCatalog", catalogManager.getUnavailableWines());
		return "/wine/catalog";
	}


	@GetMapping("/newProduct")
	String register(Model model, NewProductForm form) {

		model.addAttribute("form", form);
		model.addAttribute("wineTypes", Wine.WineType.values());
		return "wine/newProduct";
	}


	@PostMapping("/newProduct")
	String registerNew(@Valid NewProductForm form, Errors result) {

		if (result.hasErrors()) return "index";//FAILURE HINZUFÜGEN!

		Wine savedWine = catalogManager.createNewProduct(form);
		inventory.save(new UniqueInventoryItem(savedWine, Quantity.of(0)));

		return "redirect:/catalog";
	}

	@GetMapping("/wine/{wine}")
	String showProduct(@PathVariable Wine wine, Model model, NewProductForm form) {

		model.addAttribute("form", form);
		model.addAttribute("wine", wine);
		return "wine/product";
	}


	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/wine/{wine}")
	String productEdit(@PathVariable Wine wine, @Valid NewProductForm form, Errors result) {

		if (result.hasErrors()) return "index";//TODO FAILURE HINZUFÜGEN!

		if (catalogManager.editProductInCatalog(wine, form)) return "redirect:/catalog";
		else return "index";//TODO FAILURE HINZUFÜGEN!
	}


	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/wine/delete/{wine}")
	String deleteConfirm(@PathVariable Wine wine, Model model) {

		model.addAttribute("wine", wine);
		return "wine/deleteConfirmation";
	}


	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/wine/delete/confirmed/{wine}")
	String deleteItem(@PathVariable Wine wine) {

		if (inventory.findByProduct(wine).isPresent()) {
			inventory.delete(inventory.findByProduct(wine).get());
		}
		catalogManager.deleteById(wine.productId);
		return "redirect:/catalog";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/wine/deleteFromCatalog/{wine}")
	String makeItemUnavailable(@PathVariable Wine wine) {

		catalogManager.makeItemUnavailable(wine);
		return "redirect:/catalog";
	}
}
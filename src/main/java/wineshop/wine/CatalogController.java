package wineshop.wine;

import com.google.common.collect.Lists;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
	String showAvailableWines(Model model, @RequestParam Optional<String> search, @RequestParam Optional<String> sort) {
		List<Wine> items = catalogManager.getAvailableWines().toList();
		if (search.isPresent()){
			items =items.stream().filter((e) -> {
				return e.getName().toLowerCase().contains(search.get().toLowerCase());
			}).collect(Collectors.toList());
		}
		if (sort.isPresent()){
			if (sort.get().equalsIgnoreCase("A-Z"))
			items =items.stream().sorted(Comparator.comparing(Product::getName)).collect(Collectors.toList());
			if (sort.get().equalsIgnoreCase("PreisAufsteigend"))
				items =items.stream().sorted(Comparator.comparing(Product::getPrice)).collect(Collectors.toList());
			if (sort.get().equalsIgnoreCase("PreisAbsteigend"))
				items = Lists.reverse( items.stream().sorted(Comparator.comparing(Product::getPrice)).collect(Collectors.toList()));
		}

		model.addAttribute("wineCatalog", items);
		model.addAttribute("inventory", inventory);
		model.addAttribute("currentCatalog", "available");
		return "/wine/catalog";
	}

	@GetMapping("/catalogOfUnavailableProducts")
	String showUnavailableWines(Model model, @RequestParam Optional<String> search, @RequestParam Optional<String> sort) {
		List<Wine> items = catalogManager.getUnavailableWines().toList();
		if (search.isPresent()){
			items =items.stream().filter((e) -> {
				return e.getName().toLowerCase().contains(search.get().toLowerCase());
			}).collect(Collectors.toList());
		}
		if (sort.isPresent()){
			if (sort.get().equalsIgnoreCase("A-Z"))
				items =items.stream().sorted(Comparator.comparing(Product::getName)).collect(Collectors.toList());
			if (sort.get().equalsIgnoreCase("PreisAufsteigend"))
				items =items.stream().sorted(Comparator.comparing(Product::getPrice)).collect(Collectors.toList());
			if (sort.get().equalsIgnoreCase("PreisAbsteigend"))
				items = Lists.reverse( items.stream().sorted(Comparator.comparing(Product::getPrice)).collect(Collectors.toList()));
		}

		model.addAttribute("wineCatalog", items);
		model.addAttribute("inventory", inventory);
		return "/wine/unavailableProducts";
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
		if (catalogManager.isAvailable(wine)) model.addAttribute("currentWine", "available");
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

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/wine/recover/{wine}")
	String recoverItem(@PathVariable Wine wine) {


		catalogManager.makeItemAvailable(wine);
		return "redirect:/catalog";
	}


}
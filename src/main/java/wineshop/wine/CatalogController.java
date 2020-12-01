package wineshop.wine;

import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;


@Controller
class CatalogController {


	private final CatalogManager catalogManager;

	CatalogController(CatalogManager catalogManager) {
		this.catalogManager = catalogManager;
	}

	@GetMapping("/catalog")
	String showCatalog(Model model) {
		model.addAttribute("wineCatalog", catalogManager.getAllWines());

		return "/wine/catalog";
	}


	//PROVISORISCH ERSTELLTER CART-CONTROLLER
	@GetMapping("/cart")
	String getCart() {
		return "cart";
	}


	@GetMapping("/newProduct")
	String register(Model model, NewProductForm form) {
		model.addAttribute("form", form);
		model.addAttribute("wineTypes", Wine.WineType.values());
		return "wine/newProduct";
	}

	@PostMapping("/newProduct")
	String registerNew(@Valid NewProductForm form, Errors result) {

		if (result.hasErrors()) {
			return "index";//FAILURE HINZUFÜGEN!
		}


		if (catalogManager.createNewProduct(form)) return "redirect:/catalog";
		else return "index";//FAILURE HINZUFÜGEN!
	}

	@GetMapping("/wine/{productId}")
	String showProduct(@PathVariable ProductIdentifier productId, Model model, NewProductForm form) {
		Wine wine = catalogManager.findById(productId);


		model.addAttribute("form", form);
		model.addAttribute("wine", wine);
		return "wine/product";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/wine/{productId}")
	String productEdit(@PathVariable ProductIdentifier productId, @Valid NewProductForm form, Errors result) {

		Wine wine = catalogManager.findById(productId);

		if (result.hasErrors()) {
			System.out.println("Es gab Fehler");
			return "index";//FAILURE HINZUFÜGEN!
		}


		if (catalogManager.editProductInCatalog(wine.productId, form)) return "redirect:/catalog";
		else return "index";//FAILURE HINZUFÜGEN!
	}

}
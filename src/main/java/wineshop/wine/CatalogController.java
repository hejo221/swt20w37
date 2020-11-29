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


		System.out.println(form.getItemNr());
		System.out.println(form.getName());
		System.out.println(form.getPic());
		System.out.println(form.getWineType());
		System.out.println(form.getBuyPrice());
		System.out.println(form.getSellPrice());
		System.out.println(form.getDetails());

		if (result.hasErrors()) {
			System.out.println("Es gab Fehler");
			return "index";//FAILURE HINZUFÜGEN!
		}


		System.out.println("\n\nIch bin hier!");
		System.out.println(wine.getItemNr());
		System.out.println(wine.getProductId());
		System.out.println(wine.getName());
		System.out.println(wine.getPic());
		System.out.println(wine.getWineType());
		System.out.println(wine.getBuyPrice());
		System.out.println(wine.getSellPrice());
		System.out.println(wine.getDetails());
		//assert wine != null;


		System.out.println("\n\nNun bin ich da!");


		System.out.println("In CatalogController" + wine.getProductId());

		System.out.println("\n\nUnd zum Schluss da!");
		System.out.println(wine.getItemNr());
		System.out.println(wine.getProductId());
		System.out.println(wine.getName());
		System.out.println(wine.getPic());
		System.out.println(wine.getWineType());
		System.out.println(wine.getBuyPrice());
		System.out.println(wine.getSellPrice());
		System.out.println(wine.getDetails());
		if (catalogManager.editProductInCatalog(wine.productId, form)) return "redirect:/catalog";
		else return "index";//FAILURE HINZUFÜGEN!
	}


	/*@GetMapping("/addToBasket/{id}/{quantity}")
	public String addToBasket(@PathVariable ProductIdentifier id, @PathVariable Number quantity) {
		Wine wine = catalogManager.findById(id);
		System.out.println("Der Wein " + wine.getName() + " " + quantity + " wurde erfolgreich zum Warenkorb hinzugefügt.");
		return "redirect:/catalog";
	}*/

}

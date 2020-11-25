package wineshop.wine;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;
import static org.salespointframework.core.Currencies.EURO;

@Controller
class CatalogController {

	private final WineCatalog wineCatalog;


	CatalogController(WineCatalog wineCatalog) {
		this.wineCatalog = wineCatalog;
	}

	@GetMapping("/catalog")
	String showCatalog(Model model) {
		model.addAttribute("wineCatalog", wineCatalog.findAll());

		return "/wine/catalog";
	}


	//PROVISORISCH ERSTELLTER CART-CONTROLLER
	@GetMapping("/cart")
	String getCart() {
		return "cart";
	}



	@GetMapping("/wine/{itemNr}")
	String detail(@PathVariable int itemNr, Model model) {

		Streamable<Wine> liste = wineCatalog.findAll();
		for (Wine w : liste) {
			if (w.getItemNr() == itemNr)
				model.addAttribute("wine", w);
		}

		return "wine/details";
	}


	@GetMapping("/newProduct")
	String register(Model model, NewProductForm form) {
		model.addAttribute("form", form);
		model.addAttribute("wineTypes", Wine.WineType.values());
		return "wine/newProduct";
	}

	@PostMapping("/newProduct")
	String registerNew(@Valid NewProductForm form, Errors result) {
		int itemNr;
		Money buyPrice, sellPrice;

		if (result.hasErrors()) {
			return "index";//FAILURE HINZUFÜGEN!
		}

		try {
			itemNr = Integer.parseInt(form.getItemNr());
			buyPrice = Money.of(Double.parseDouble(form.getBuyPrice()), EURO);
			sellPrice = Money.of(Double.parseDouble(form.getSellPrice()), EURO);
		} catch (Exception exception) {
			return "index"; //FAILURE HINZUFÜGEN
		}


		wineCatalog.save(new Wine(itemNr, form.getName(), form.getWineType(), form.getPic(), buyPrice, sellPrice, form.getDetails()));

		return "redirect:catalog";
	}
}

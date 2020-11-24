package wineshop.wine;

import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/wine")
class WineController {

	private static final Quantity NONE = Quantity.of(0);

	private WineRepository wineRepository;
	private final WineManager wineManager;


	WineController(WineRepository wineRepository, WineManager wineManager) {
		this.wineRepository = wineRepository;
		this.wineManager = wineManager;
	}

	@GetMapping("/catalog")
	String redCatalog(Model model) {
		model.addAttribute("wines", wineRepository.findAll());

		return "/wine/catalog";
	}

	@GetMapping("/add")
	public String add(Model model, WineRegistrationForm form) {
		model.addAttribute("form", form);
		return "/wine/add";
	}

	@PostMapping("/add")
	public String addWine(@Valid WineRegistrationForm form) {
		wineManager.addWine(form);

		return "redirect:/wine/catalog";
	}
}

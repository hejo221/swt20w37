package kickstart.controller;

import kickstart.model.Comment;
import kickstart.model.Wine;
import kickstart.repository.WineRepository;
import org.hibernate.validator.constraints.Range;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Controller
class CatalogController {

	private static final Quantity NONE = Quantity.of(0);

	private final WineRepository catalog;
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final BusinessTime businessTime;

	@Autowired
	private WineRepository wineRepository;

	CatalogController(WineRepository wineRepository, UniqueInventory<UniqueInventoryItem> inventory,
					  BusinessTime businessTime) {

		this.catalog = wineRepository;
		this.inventory = inventory;
		this.businessTime = businessTime;
	}

	/*
	@GetMapping("/white")
	String catalog(Model model, @PageableDefault(size = 2) Pageable pageable,
						@RequestParam(required = false, defaultValue = "") String searchName) {

		Page<Wine> wines = wineRepository.findByName(searchName, pageable);
		int startPage = Math.max(1, wines.getPageable().getPageNumber() - 4);
		int endPage = Math.min(wines.getTotalPages(), wines.getPageable().getPageNumber() + 4);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("wines", wines);

		model.addAttribute("catalog", catalog.findByType(Wine.WineType.WHITE));
		model.addAttribute("title", "catalog.white.title");

		return "/catalog/catalog";
	}
	*/

	@GetMapping("/catalog")
	String redCatalog(Model model) {

		model.addAttribute("wines", catalog.findAll());

		return "/catalog/catalog";
	}

	@GetMapping("/catalog/{wine}")
	String detail(@PathVariable Wine wine, Model model) {

		var quantity = inventory.findByProductIdentifier(wine.getId()) //
				.map(InventoryItem::getQuantity) //
				.orElse(NONE);

		model.addAttribute("wine", wine);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", quantity.isGreaterThan(NONE));

		return "/catalog/detail";
	}

	@PostMapping("/catalog/{wine}/comments")
	public String comment(@PathVariable Wine wine, @Valid CommentAndRating payload) {

		wine.addComment(payload.toComment(businessTime.getTime()));
		catalog.save(wine);

		return "redirect:/wine/" + wine.getId();
	}

	interface CommentAndRating {

		@NotEmpty
		String getComment();

		@Range(min = 1, max = 5)
		int getRating();

		default Comment toComment(LocalDateTime time) {
			return new Comment(getComment(), getRating(), time);
		}
	}
}

package wineshop.user;

import com.google.common.collect.Lists;
import org.salespointframework.catalog.Product;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import wineshop.wine.Wine;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
class UserController {

	private final UserManager userManager;
	private final UserRepository userRepository;


	UserController(UserManager userManager, UserRepository userRepository) {
		Assert.notNull(userManager, "CustomerManagement must not be null!");

		this.userManager = userManager;
		this.userRepository = userRepository;
	}


	@GetMapping("/login")
	public String login() {
		return "user/login";
	}

	@GetMapping("/register")
	String register(Model model, UserRegistrationForm form) {
		model.addAttribute("form", form);
		return "/user/register";
	}

	@PostMapping("/register")
	String registerNew(@Valid UserRegistrationForm form, Errors result) {

		if (result.hasErrors()) {
			return "/user/register";
		}

		userManager.createAccount(form);

		return "redirect:/user/list";
	}

	@GetMapping("/list")
	@PreAuthorize("hasRole('ADMIN')")
	String users(Model model, @RequestParam Optional<String> search, @RequestParam Optional<String> sort) {

		List<User> items = userRepository.findAll().toList();
		if (search.isPresent()){
			items =items.stream().filter((e) -> {
				return e.getUserAccount().getUsername().toLowerCase().contains(search.get().toLowerCase());
			}).collect(Collectors.toList());
		}
		if (sort.isPresent()){
			if (sort.get().equalsIgnoreCase("A-Z Angestellte"))
				items =items.stream().sorted(Comparator.comparing(e -> e.getUserAccount().getUsername())).collect(Collectors.toList());

			if (sort.get().equalsIgnoreCase("A-Z Vorname"))
				items =items.stream().sorted(Comparator.comparing(e -> e.getUserAccount().getFirstname())).collect(Collectors.toList());
			if (sort.get().equalsIgnoreCase("A-Z Nachname"))
				items =items.stream().sorted(Comparator.comparing(e -> e.getUserAccount().getLastname())).collect(Collectors.toList());
		}

		model.addAttribute("users", items);

		return "/user/users";
	}

	@GetMapping("/edit/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public String edit(Model model, @PathVariable Long id) {
		User user;
		model.addAttribute("user", user = this.userRepository.findById(id).get());
		model.addAttribute("username", user.getUsername());
		model.addAttribute("firstName", user.getUserAccount().getFirstname());
		model.addAttribute("lastName", user.getUserAccount().getLastname());
		return "/user/edit";
	}

	@PostMapping("/save")
	public String save(@RequestParam("username") String username, @RequestParam("firstName") String firstname,
					   @RequestParam("lastName") String lastname, @RequestParam("id") Long id) {
		User user = userRepository.findById(id).get();

		user.setUsername(username);
		user.getUserAccount().setFirstname(firstname);
		user.getUserAccount().setLastname(lastname);

		this.userRepository.save(user);

		return "redirect:/user/list";
	}

	@Transactional
	@PostMapping("/delete")
	public String delete(@RequestParam("id") Long id) {
		this.userRepository.deleteById(id);
		return "redirect:/user/list";
	}
}

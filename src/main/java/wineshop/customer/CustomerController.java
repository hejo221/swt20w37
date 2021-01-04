package wineshop.customer;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.validation.Valid;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private final CustomerManager customerManager;
	private final CustomerRepository customerRepository;

	public CustomerController(CustomerManager customerManager, CustomerRepository customerRepository) {
		Assert.notNull(customerManager, "CustomerManagement must not be null!");

		this.customerManager = customerManager;
		this.customerRepository = customerRepository;
	}

	@GetMapping("/list")
	public String customers(Model model) {
		model.addAttribute("customers", customerRepository.findAll());

		return "/customer/customers";
	}

	@GetMapping("/register")
	public String registerCustomer(Model model, CustomerRegistrationForm form) {
		model.addAttribute("form", form);
		return "/customer/register";
	}

	@PostMapping("/register")
	public String registerNew(@Valid CustomerRegistrationForm form, Errors result) {

		if (result.hasErrors()) {
			return "/customer/register";
		}
		customerManager.createCustomer(form);

		return "redirect:/customer/list";
	}

	@GetMapping("/edit/{id}")
	public String editCustomer(Model model, @PathVariable Long id) {
		Customer customer;
		model.addAttribute("customer", customer = this.customerRepository.findById(id).get());
		model.addAttribute("firstName", customer.getFirstName());
		model.addAttribute("familyName", customer.getFamilyName());
		model.addAttribute("mail", customer.getEmail());
		model.addAttribute("address", customer.getAddress());
		return "/customer/edit";
	}

	@PostMapping("/save")
	public String saveCustomer(@RequestParam("firstName") String firstName, @RequestParam("familyName") String familyName,
							   @RequestParam("email") String email, @RequestParam("address") String address, @RequestParam("id") Long id) {
		Customer customer = customerRepository.findById(id).get();

		customer.setFirstName(firstName);
		customer.setFamilyName(familyName);
		customer.setEmail(email);
		customer.setAddress(address);
		this.customerRepository.save(customer);

		return "redirect:/customer/list";
	}

	@Transactional
	@PostMapping("/delete")
	public String deleteCustomer(@RequestParam("id") Long id) {
		this.customerRepository.deleteCustomerById(id);
		return "redirect:/customer/list";
	}


}

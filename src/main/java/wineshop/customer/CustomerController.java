/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
class CustomerController {

	private final CustomerManager customerManager;
	private final CustomerRepository customerRepository;

	CustomerController(CustomerManager customerManager, CustomerRepository customerRepository) {
		Assert.notNull(customerManager, "CustomerManagement must not be null!");

		this.customerManager = customerManager;
		this.customerRepository = customerRepository;
	}

	@GetMapping("/list")
	String customers(Model model) {
		model.addAttribute("customers", customerRepository.findAll());

		return "/customer/customers";
	}

	@GetMapping("/register")
	String register(Model model, CustomerRegistrationForm form) {
		model.addAttribute("form", form);
		return "/customer/register";
	}

	@PostMapping("/register")
	String registerNew(@Valid CustomerRegistrationForm form) {
		customerManager.createCustomer(form);

		return "redirect:/customer/list";
	}

	@GetMapping("/edit/{id}")
	public String edit(Model model, @PathVariable Long id) {
		Customer customer = this.customerRepository.findById(id).get();
		model.addAttribute("customer", customer = this.customerRepository.findById(id).get());
		model.addAttribute("firstName", customer.getUserAccount().getFirstname());
		model.addAttribute("lastName", customer.getUserAccount().getLastname());
		model.addAttribute("mail", customer.getUserAccount().getEmail());
		model.addAttribute("address", customer.getAddress());
		return "/customer/edit";
	}

	@PostMapping("/save")
	public String save(@RequestParam("firstName") String firstname, @RequestParam("lastName") String lastname,
									  @RequestParam("mail") String email, @RequestParam("address") String address, @RequestParam("id") Long id) {
		Customer customer = customerRepository.findById(id).get();

		customer.getUserAccount().setFirstname(firstname);
		customer.getUserAccount().setLastname(lastname);
		customer.getUserAccount().setEmail(email);
		customer.setAddress(address);
		this.customerRepository.save(customer);

		return "redirect:/customer/list";
	}

	@Transactional
	@PostMapping("/delete")
	public String delete(@RequestParam("id") Long id) {
		this.customerRepository.deleteById(id);
		return "redirect:/customer/list";
	}
}

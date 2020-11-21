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
package kickstart.controller;

import kickstart.manager.CustomerManager;
import kickstart.model.CustomerRegistrationForm;
import kickstart.model.UserRegistrationForm;
import kickstart.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

		model.addAttribute("customers", customerManager.findAll());

		return "/customer/customers";
	}

	@GetMapping("/add")
	String register(Model model, CustomerRegistrationForm form) {
		model.addAttribute("form", form);
		return "/customer/register";
	}

	@PostMapping("/add")
	String registerNew(@Valid CustomerRegistrationForm form, Errors result) {

		if (result.hasErrors()) {
			return "/customer/register";
		}

		customerManager.createCustomer(form);

		return "redirect:/customer/list";
	}
}

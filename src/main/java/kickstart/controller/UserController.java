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

import kickstart.manager.UserManager;
import kickstart.model.UserRegistrationForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
class UserController {

	private final UserManager userManager;

	UserController(UserManager customerManagement) {

		Assert.notNull(customerManagement, "CustomerManagement must not be null!");

		this.userManager = customerManagement;
	}

	@GetMapping("/login")
	public String login() {
		return "user/login";
	}

	@PostMapping("/register")
	String registerNew(@Valid UserRegistrationForm form, Errors result) {

		if (result.hasErrors()) {
			return "/user/register";
		}

		userManager.createAccount(form);

		return "redirect:/";
	}

	@GetMapping("/register")
	String register(Model model, UserRegistrationForm form) {
		model.addAttribute("form", form);
		return "/user/register";
	}

	@GetMapping("/customers")
	@PreAuthorize("hasRole('BOSS')")
	String customers(Model model) {

		model.addAttribute("customerList", userManager.findAll());

		return "customers";
	}
}

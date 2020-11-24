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
package wineshop.user;

import org.salespointframework.useraccount.Password;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.validation.Valid;

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
	String registerNew(@Valid UserRegistrationForm form) {
		userManager.createAccount(form);

		return "redirect:/user/list";
	}

	@GetMapping("/list")
	@PreAuthorize("hasRole('ADMIN')")
	String users(Model model) {
		model.addAttribute("users", userManager.findAll());

		return "/user/users";
	}

	@GetMapping("/edit/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public String edit(Model model, @PathVariable Long id) {
		User user = this.userRepository.findById(id).get();
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

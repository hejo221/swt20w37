/*
q * Copyright 2017-2019 the original author or authors.
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

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;


@Component
class UserDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(UserDataInitializer.class);

	private final UserAccountManagement userAccountManagement;
	private final UserManager userManager;

	UserDataInitializer(UserAccountManagement userAccountManagement, UserManager userManager) {
		Assert.notNull(userAccountManagement, "UserAccountManagement must not be null!");
		Assert.notNull(userManager, "CustomerRepository must not be null!");

		this.userAccountManagement = userAccountManagement;
		this.userManager = userManager;
	}


	@Override
	public void initialize() {
		if (userAccountManagement.findByUsername("admin").isPresent()) {
			return;
		}

		LOG.info("Creating default users and customers.");

		userAccountManagement.create("admin", UnencryptedPassword.of("123"), Role.of("ADMIN"));

		var password = "123";

		List.of(
				new UserRegistrationForm("hans", password, "Max", "Knoblauch"),
				new UserRegistrationForm("dextermorgan", password, "Mann", "Herrmann"),
				new UserRegistrationForm("earlhickey", password, "Lala", "Land"),
				new UserRegistrationForm("mclovinfogell", password, "He", "She")//
		).forEach(userManager::createAccount);

	}
}

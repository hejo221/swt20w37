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
package kickstart.datainitializer;

import kickstart.manager.UserManager;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Initializes default user accounts and customers. The following are created:
 * <ul>
 * <li>An admin user named "boss".</li>
 * <li>The customers "hans", "dextermorgan", "earlhickey", "mclovinfogell" backed by user accounts with the same
 * name.</li>
 * </ul>
 *
 * @author Oliver Gierke
 */
@Component
@Order(10)
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

	/*
	 * (non-Javadoc)
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {

		// Skip creation if database was already populated
		if (userAccountManagement.findByUsername("boss").isPresent()) {
			return;
		}

		LOG.info("Creating default users and customers.");

		userAccountManagement.create("boss", UnencryptedPassword.of("123"), Role.of("BOSS"));

		var password = "123";
/*
		List.of(//

				new AccountRegistrationForm("hans", password, "wurst"),
				new AccountRegistrationForm("dextermorgan", password, "Miami-Dade County"),
				new AccountRegistrationForm("earlhickey", password, "Camden County - Motel"),
				new AccountRegistrationForm("mclovinfogell", password, "Los Angeles")//


		).forEach(accountService::createAccount);
						 */

	}
}

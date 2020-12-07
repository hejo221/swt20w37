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
package wineshop.customer;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
class CustomerDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerDataInitializer.class);

	private final UserAccountManagement userAccountManagement;
	private final CustomerManager customerManager;

	CustomerDataInitializer(UserAccountManagement userAccountManagement, CustomerManager customerManager) {
		Assert.notNull(userAccountManagement, "UserAccountManagement must not be null!");
		Assert.notNull(customerManager, "CustomerRepository must not be null!");

		this.userAccountManagement = userAccountManagement;
		this.customerManager = customerManager;
	}

	@Override
	public void initialize() {
		LOG.info("Creating default users and customers.");

		List.of(//
				new CustomerRegistrationForm("Hans","Max", "max@dmail.com", "Zur Schönen Gelegenheit 5, 92224 Amberg"),
				new CustomerRegistrationForm("Herrmann","Kritzschke", "herrmann@dmail.com", "Zur Hölle 666, 31785 Hameln"),
				new CustomerRegistrationForm("Martina","Knoblauch", "knoblauch@dmail.com", "Im Paradies 2, 47800 Krefeld"),
				new CustomerRegistrationForm("Bärbel","Zuck", "zuck@dmail.com", "Beamtenlaufbahn 123, 22850 Norderstedt")
		).forEach(customerManager::createCustomer);
	}
}

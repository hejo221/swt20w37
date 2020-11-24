/*
 * Copyright 2017-2019 the original author or authors.
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

import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@Service
@Transactional
public class CustomerManager {

	public static final Role CUSTOMER_ROLE = Role.of("CUSTOMER");

	private final CustomerRepository customerRepository;
	private final UserAccountManagement userAccounts;

	CustomerManager(CustomerRepository customerRepository, UserAccountManagement userAccounts) {
		Assert.notNull(customerRepository, "CustomerRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.customerRepository = customerRepository;
		this.userAccounts = userAccounts;
	}

	public Customer createCustomer(CustomerRegistrationForm form) {
		Assert.notNull(form, "Registration form must not be null!");

		String name = form.getFirstname() + form.getLastname();
		var password = UnencryptedPassword.of("1");
		var userAccount = userAccounts.create(name, password, form.getEmail(), CUSTOMER_ROLE);

		userAccount.setFirstname(form.getFirstname());
		userAccount.setLastname(form.getLastname());

		return customerRepository.save(new Customer(userAccount, form.getAddress()));
	}

}

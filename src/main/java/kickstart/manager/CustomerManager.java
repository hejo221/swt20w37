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
package kickstart.manager;

import kickstart.model.Customer;
import kickstart.model.CustomerRegistrationForm;
import kickstart.repository.CustomerRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Implementation of business logic related to {@link Customer}s.
 *
 * @author Oliver Gierke
 */
@Service
@Transactional
public class CustomerManager {

	public static final Role CUSTOMER_ROLE = Role.of("CUSTOMER");

	private final CustomerRepository customers;
	private final UserAccountManagement userAccounts;

	CustomerManager(CustomerRepository customers, UserAccountManagement userAccounts) {

		Assert.notNull(customers, "CustomerRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.customers = customers;
		this.userAccounts = userAccounts;
	}

	public Customer createCustomer(CustomerRegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		String name = form.getFirstname() + form.getLastname();
		var password = UnencryptedPassword.of("1");
		var userAccount = userAccounts.create(name, password, form.getEmail(), CUSTOMER_ROLE);

		userAccount.setFirstname(form.getFirstname());
		userAccount.setLastname(form.getLastname());

		return customers.save(new Customer(userAccount, form.getAddress()));
	}

	public Streamable<Customer> findAll() {
		return customers.findAll();
	}
}

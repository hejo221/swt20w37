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

import kickstart.model.UserRegistrationForm;
import kickstart.model.User;
import kickstart.repository.UserRepository;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@Service
@Transactional
public class UserManager {

	public static final Role CUSTOMER_ROLE = Role.of("EMPLOYEE");

	private final UserRepository account;
	private final UserAccountManagement userAccounts;

	UserManager(UserRepository account, UserAccountManagement userAccounts) {

		Assert.notNull(account, "AccountRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.account = account;
		this.userAccounts = userAccounts;
	}

	public User createAccount(UserRegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		var password = UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getUsername(), password, CUSTOMER_ROLE);

		return account.save(new User(userAccount, form.getUsername()));
	}

	public Streamable<User> findAll() {
		return account.findAll();
	}
}

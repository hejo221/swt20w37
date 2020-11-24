/*
 * Copyright 2013-2019 the original author or authors.
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

import javax.validation.constraints.NotEmpty;

public class UserRegistrationForm {

	@NotEmpty(message = "{RegistrationForm.address.NotEmpty}") // s
	private final String username;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}") //
	private final String password;

	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}") //
	private final String firstname;

	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}") //
	private final String lastname;

	public UserRegistrationForm(String username, String password, String firstname, String lastname) {

		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {return lastname;}

}
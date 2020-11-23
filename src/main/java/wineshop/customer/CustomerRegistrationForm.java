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
package wineshop.customer;

import javax.validation.constraints.NotEmpty;

public class CustomerRegistrationForm {

	@NotEmpty(message = "{RegistrationForm.address.NotEmpty}") // s
	private final String firstname;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}") //
	private final String lastname;

	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}") //
	private final String email;

	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}") //
	private final String address;


	public CustomerRegistrationForm(String firstname, String lastname, String email, String address) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.address = address;
	}


	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {return address;}
}

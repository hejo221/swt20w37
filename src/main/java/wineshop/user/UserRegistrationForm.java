package wineshop.user;

import javax.validation.constraints.NotEmpty;

public class UserRegistrationForm {

	@NotEmpty
	private final String username;

	@NotEmpty
	private final String password;

	@NotEmpty
	private final String firstname;

	@NotEmpty
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

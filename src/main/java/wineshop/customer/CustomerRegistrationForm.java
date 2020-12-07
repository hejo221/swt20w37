package wineshop.customer;

import javax.validation.constraints.NotEmpty;

public class CustomerRegistrationForm {

	@NotEmpty
	private final String firstName;

	@NotEmpty
	private final String familyName;

	@NotEmpty
	private final String email;

	@NotEmpty
	private final String address;


	public CustomerRegistrationForm(String firstName, String familyName, String email, String address) {
		this.firstName = firstName;
		this.familyName = familyName;
		this.email = email;
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {return address;}
}
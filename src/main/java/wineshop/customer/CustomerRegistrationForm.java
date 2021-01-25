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
	private final String street;

	@NotEmpty
	private final String zipCode;

	@NotEmpty
	private final String city;


	public CustomerRegistrationForm(String firstName, String familyName, String email, String street, String zipCode, String city) {
		this.firstName = firstName;
		this.familyName = familyName;
		this.email = email;
		this.street = street;
		this.zipCode = zipCode;
		this.city = city;
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

	public String getStreet() {
		return street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getCity() {
		return city;
	}
}
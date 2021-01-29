package wineshop.customer;

import javax.validation.constraints.NotEmpty;

public class CustomerRegistrationForm {

	@NotEmpty
	private final String formFirstName;

	@NotEmpty
	private final String formFamilyName;

	@NotEmpty
	private final String formEmail;

	@NotEmpty
	private final String formStreet;

	@NotEmpty
	private final String formZipCode;

	@NotEmpty
	private final String formCity;


	public CustomerRegistrationForm(String formFirstName, String formFamilyName, String formEmail, String formStreet,
									String formZipCode, String formCity) {
		this.formFirstName = formFirstName;
		this.formFamilyName = formFamilyName;
		this.formEmail = formEmail;
		this.formStreet = formStreet;
		this.formZipCode = formZipCode;
		this.formCity = formCity;
	}

	public String getFormFirstName() {
		return formFirstName;
	}

	public String getFormFamilyName() {
		return formFamilyName;
	}

	public String getFormEmail() {
		return formEmail;
	}

	public String getFormStreet() {
		return formStreet;
	}

	public String getFormZipCode() {
		return formZipCode;
	}

	public String getFormCity() {
		return formCity;
	}
}
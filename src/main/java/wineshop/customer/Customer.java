package wineshop.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.util.Assert;


@Entity
public class Customer {

	private @Id @GeneratedValue long id;


	private String firstName;
	private String familyName;
	private String email;
	private String street;
	private String zipCode;
	private String city;

	@SuppressWarnings("unused")
	public Customer() {}

	public Customer(String firstName, String familyName, String email, String street, String zipCode, String city) {

		Assert.hasText(firstName, "FirstName must not be null or empty!");
		Assert.hasText(familyName, "FamilyName must not be null or empty!");
		Assert.hasText(email, "Email must not be null or empty!");
		Assert.hasText(street, "Street must not be null or empty!");
		Assert.hasText(zipCode, "ZipCode must not be null or empty!");
		Assert.hasText(city, "City must not be null or empty!");

		this.firstName = firstName;
		this.familyName = familyName;
		this.email = email;
		this.street = street;
		this.zipCode = zipCode;
		this.city = city;
	}

	public long getId() {
		return id;
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

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public void setCity(String city) {
		this.city = city;
	}
}

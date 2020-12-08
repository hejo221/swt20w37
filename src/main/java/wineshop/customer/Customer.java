package wineshop.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Customer {

	private @Id @GeneratedValue long id;

	private String firstName;
	private String familyName;
	private String email;
	private String address;

	@SuppressWarnings("unused")
	public Customer() {}

	public Customer(String firstName, String familyName, String email, String address) {
		this.firstName = firstName;
		this.familyName = familyName;
		this.email = email;
		this.address = address;
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

	public String getAddress() {
		return address;
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

	public void setAddress(String address) {
		this.address = address;
	}
}

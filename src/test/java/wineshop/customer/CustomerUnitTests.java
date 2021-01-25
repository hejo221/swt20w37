package wineshop.customer;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class CustomerUnitTests {

	@Test
	public void rejectsEmptyFirstName() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("", "familyName", "email", "street", "zipCode", "city"));
	}

	@Test
	public void rejectsEmptyFamilyName() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("firstName", "", "email", "street", "zipCode", "city"));
	}

	@Test
	public void rejectsEmptyEmail() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("firstName", "familyName", "", "street", "zipCode", "city"));
	}

	@Test
	public void rejectsEmptyStreet() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("firstName", "familyName", "email", "", "zipCode", "city"));
	}

	@Test
	public void rejectsEmptyZipCode() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("", "familyName", "email", "street", "", "city"));
	}

	@Test
	public void rejectsEmptyCity() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("", "familyName", "email", "street", "zipCode", ""));
	}

}

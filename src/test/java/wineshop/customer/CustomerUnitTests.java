package wineshop.customer;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class CustomerUnitTests {

	@Test
	public void rejectsEmptyFirstName() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("", "familyName", "email", "address"));
	}

	@Test
	public void rejectsEmptyFamilyName() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("firstName", "", "email", "address"));
	}

	@Test
	public void rejectsEmptyEmail() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("firstName", "familyName", "", "address"));
	}

	@Test
	public void rejectsEmptyAddress() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Customer("firstName", "familyName", "email", ""));
	}

}

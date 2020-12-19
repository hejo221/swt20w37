package wineshop.customer;

import static org.assertj.core.api.Assertions.*;

import wineshop.AbstractIntegrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerRepositoryIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	CustomerRepository customerRepository;

	@Test
	public void persistsCustomer() {

		Customer customer = new Customer("firstName", "familyName", "email", "address");

		customerRepository.save(customer);

		assertThat(customerRepository.findAll()).contains(customer);
	}

	@Test
	public void findsCustomerById() {

		Customer customer = new Customer("firstName", "familyName", "email", "address");

		customerRepository.save(customer);

		assertThat(customerRepository.findById(customer.getId())).contains(customer);
	}


}

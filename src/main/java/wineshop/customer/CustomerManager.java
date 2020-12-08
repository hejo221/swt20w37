package wineshop.customer;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import wineshop.user.User;


@Service
@Transactional
public class CustomerManager {

	private final CustomerRepository customerRepository;

	CustomerManager(CustomerRepository customerRepository) {
		Assert.notNull(customerRepository, "CustomerRepository must not be null!");

		this.customerRepository = customerRepository;
	}

	public Customer createCustomer(CustomerRegistrationForm form) {
		Assert.notNull(form, "Form must not be null!");

		String firstName = form.getFirstName();
		String familyName = form.getFamilyName();
		String address = form.getAddress();
		String email = form.getEmail();


		return customerRepository.save(new Customer(firstName, familyName, email, address));
	}


	public Streamable<Customer> findAll() {
		return customerRepository.findAll();
	}

}
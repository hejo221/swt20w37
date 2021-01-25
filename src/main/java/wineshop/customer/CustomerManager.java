package wineshop.customer;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
		String email = form.getEmail();
		String street = form.getStreet();
		String zipCode = form.getZipCode();
		String city = form.getCity();

		return customerRepository.save(new Customer(firstName, familyName, email, street, zipCode, city));
	}

	public Streamable<Customer> findAll() {
		return customerRepository.findAll();
	}


	public Customer findCustomerById(Long id){
		return customerRepository.findById(id).get();
	}

}
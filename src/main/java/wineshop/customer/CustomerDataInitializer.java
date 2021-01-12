package wineshop.customer;

import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class CustomerDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerDataInitializer.class);

	private final CustomerManager customerManager;

	public CustomerDataInitializer(CustomerManager customerManager) {
		Assert.notNull(customerManager, "CustomerRepository must not be null!");

		this.customerManager = customerManager;
	}

	@Override
	public void initialize() {

		if (customerManager.findAll().stream().count() > 0) {
			return;
		}

		LOG.info("Creating default customers.");

		List.of(
				new CustomerRegistrationForm("Hans","Max", "max@dmail.com", "Zur Schönen Gelegenheit 5, 92224 Amberg"),
				new CustomerRegistrationForm("Herrmann","Kritzschke", "herrmann@dmail.com", "Zur Hölle 666, 31785 Hameln"),
				new CustomerRegistrationForm("Martina","Knoblauch", "knoblauch@dmail.com", "Im Paradies 2, 47800 Krefeld"),
				new CustomerRegistrationForm("Bärbel","Zuck", "zuck@dmail.com", "Beamtenlaufbahn 123, 22850 Norderstedt")
		).forEach(customerManager::createCustomer);
	}
}

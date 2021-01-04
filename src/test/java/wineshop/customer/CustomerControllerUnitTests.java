package wineshop.customer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Streamable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/* @ExtendWith(MockitoExtension.class)
public class CustomerControllerUnitTests {

	@Mock
	CustomerManager customerManager;

	@Mock
	CustomerRepository customerRepository;

	@Test
	public void addsToModelForCustomer() {

		Customer customer = new Customer("firstName", "familyName", "email", "address");
		doReturn(Streamable.of(customer)).when(customerRepository).findAll();

		Model customerModel = new ExtendedModelMap();

		CustomerController customerController = new CustomerController(customerManager, customerRepository);

		String customersViewName = customerController.customers(customerModel);

		assertThat(customersViewName).isEqualTo("/customer/customers");
		assertThat(customerModel.asMap().get("customers")).isInstanceOf(Iterable.class);

		String registerViewName = customerController.registerCustomer(customerModel, new CustomerRegistrationForm(null, null, null, null));

		assertThat(registerViewName).isEqualTo("/customer/register");
		assertThat(customerModel.asMap().get("form")).isNotNull();

		verify(customerRepository, times(1)).findAll();
	}


}
*/
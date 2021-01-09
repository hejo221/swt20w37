package wineshop.order;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import wineshop.customer.CustomerController;
import wineshop.customer.CustomerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderCustControllerIntegrationTests {

	@Autowired
	OrderCustController orderCustController;

/*	@Mock
	ProductIdentifier id;


	@Test
	void addToCart() {
		assertThat(orderCustController.addToCart(id, 10, orderCustController.initializeCart())).isEqualTo("redirect:/catalog");
	}*/
}

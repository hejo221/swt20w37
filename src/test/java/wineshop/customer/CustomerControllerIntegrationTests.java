package wineshop.customer;

import static org.assertj.core.api.Assertions.*;

import wineshop.AbstractIntegrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;

import java.util.Optional;

public class CustomerControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	CustomerController customerController;

	@Test
	@WithMockUser(roles = "ADMIN")
	void allowsAuthenticatedAccessToController() {

		ExtendedModelMap model = new ExtendedModelMap();

		customerController.customers(model, Optional.of(""), Optional.of(""));

		assertThat(model.get("customers")).isNotNull();
	}

}

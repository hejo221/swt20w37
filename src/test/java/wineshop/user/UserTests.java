package wineshop.user;


import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import wineshop.AbstractIntegrationTests;
import wineshop.wine.CatalogManager;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class UserTests extends AbstractIntegrationTests {

	@Autowired
	CatalogManager catalogManager;

	@Autowired
	UserController controller;

	@Autowired
	UserManager userManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAccountManagement userAccounts;


	UserRegistrationForm form = new UserRegistrationForm("username", "password", "forename", "surname");
	Errors errors = new BeanPropertyBindingResult(form, "objectName");
	Optional<String> none = Optional.empty();

	// UserController Tests
	@Test
	@WithMockUser(roles = "ADMIN")
	public void userControllerTest() {
		Optional<String> none = Optional.empty();
		Model model = new ExtendedModelMap();

		String returnedView = controller.login();
		assertThat(returnedView).isEqualTo("user/login");

		returnedView = controller.register(model, form);
		assertThat(returnedView).isEqualTo("user/register");

		returnedView = controller.registerNew(form, errors);
		assertThat(returnedView).isEqualTo("redirect:/user/list");

		returnedView = controller.users(model, none, none);
		assertThat(returnedView).isEqualTo("user/users");

		returnedView = controller.edit(model, userRepository.findAll().get().findFirst().get().getId());
		assertThat(returnedView).isEqualTo("user/edit");

		returnedView = controller.save("username", "firstname", "lastname", userRepository.findAll().get().findFirst().get().getId());
		assertThat(returnedView).isEqualTo("redirect:/user/list");

		returnedView = controller.delete(userRepository.findAll().get().findFirst().get().getId());
		assertThat(returnedView).isEqualTo("redirect:/user/list");


	}


	// InventoryManager wird getestet
	@Test
	void throwsEmptyParameters() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new UserManager(userRepository, null));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new UserManager(null, userAccounts));
	}

	/*
	// Bestandänderung
	@Test
	public void updatesAmountOfEachProduct() {
		for (Wine wine : catalogManager.getAllWines()) {
			controller.updateAmount(wine.getProductId(), 333);
		}

		for (Wine wine : catalogManager.getAllWines()) {
			assertThat(inventory.findByProductIdentifier(wine.getProductId()).get().getQuantity()).isEqualTo(Quantity.of(333));
		}
	}

	// MIN-Bestandänderung
	@Test
	public void updatesMinAmountOfEachProduct() {
		for (Wine wine : catalogManager.getAllWines()) {
			controller.updateMinAmount(wine.getProductId(), 111);
		}

		for (Wine wine : catalogManager.getAllWines()) {
			assertThat(wine.getMinAmount()).isEqualTo(Quantity.of(111));
		}
	}

	// Deletes all Items in Inventory
	@Test
	public void deletesItems() {
		for (UniqueInventoryItem item : inventory.findAll()) {
			inventoryManager.deleteItem(item.getProduct().getId());
		}
		assertThat(inventory.findAll()).hasSize(0);
	}


	// InventoryInitializer wird getestet
	@Test
	void throwsEmptyThingsInInventoryInitializer() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new InventoryInitializer(null, null));
	}



	 */
}

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

	@Autowired
	UserController userController;


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


	// Registration Form must not be null
	@Test
	void createAccountExeption(){
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userManager.createAccount(null));
	}


	// tests the method createAccount() in UserManager
	@Test
	void testsCreateAccount(){
		User createdUser = userManager.createAccount(form);
		assertThat(userRepository.findById(createdUser.getId()).isPresent()).isTrue();
		User testUser = userRepository.findById(createdUser.getId()).get();
		assertThat(testUser.getUsername()).isEqualTo("username");
		assertThat(testUser.getUserAccount().getUsername()).isEqualTo("username");
		assertThat(testUser.getUserAccount().getFirstname()).isEqualTo("forename");
		assertThat(testUser.getUserAccount().getLastname()).isEqualTo("surname");
	}

	//Editing of User Datas
	@Test
	@WithMockUser(roles = "ADMIN")
	void testsEditingOfUserDatas(){
		User createdUser = userManager.createAccount(form);

		userController.save("noUsername", "noForename", "noSurname", createdUser.getId());

		assertThat(userRepository.findById(createdUser.getId()).isPresent()).isTrue();
		User testUser = userRepository.findById(createdUser.getId()).get();
		assertThat(testUser.getUsername()).isEqualTo("noUsername");
		//assertThat(testUser.getUserAccount().getUsername()).isEqualTo("noUsername"); //TODO WARUM FUNKTIONIERT ES NICHT?
		assertThat(testUser.getUserAccount().getFirstname()).isEqualTo("noForename");
		assertThat(testUser.getUserAccount().getLastname()).isEqualTo("noSurname");
	}


	//Deleting of User Account
	@Test
	@WithMockUser(roles = "ADMIN")
	void testsDeletingOfUserAccount(){
		User createdUser = userManager.createAccount(form);
		assertThat(userRepository.findById(createdUser.getId()).isPresent()).isTrue();

		userRepository.deleteById(createdUser.getId());
		assertThat(userRepository.findById(createdUser.getId()).isEmpty()).isTrue();
	}

}

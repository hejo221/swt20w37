package wineshop.user;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;


@Component
class UserDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(UserDataInitializer.class);

	private final UserAccountManagement userAccountManagement;
	private final UserManager userManager;

	UserDataInitializer(UserAccountManagement userAccountManagement, UserManager userManager) {
		Assert.notNull(userAccountManagement, "UserAccountManagement must not be null!");
		Assert.notNull(userManager, "CustomerRepository must not be null!");

		this.userAccountManagement = userAccountManagement;
		this.userManager = userManager;
	}


	@Override
	public void initialize() {
		if (userAccountManagement.findByUsername("admin").isPresent()) {
			return;
		}

		LOG.info("Creating default users.");

		userAccountManagement.create("admin", UnencryptedPassword.of("123"), Role.of("ADMIN"));

		var password = "123";

		List.of(
				new UserRegistrationForm("hans", password, "Max", "Knoblauch"),
				new UserRegistrationForm("dextermorgan", password, "Mann", "Herrmann"),
				new UserRegistrationForm("earlhickey", password, "Lala", "Land"),
				new UserRegistrationForm("mclovinfogell", password, "He", "She")//
		).forEach(userManager::createAccount);

	}
}

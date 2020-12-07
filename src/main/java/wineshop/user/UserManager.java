package wineshop.user;

import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@Service
@Transactional
public class UserManager {

	public static final Role USER_ROLE = Role.of("EMPLOYEE");

	private final UserRepository userRepository;
	private final UserAccountManagement userAccounts;

	UserManager(UserRepository userRepository, UserAccountManagement userAccounts) {
		Assert.notNull(userRepository, "AccountRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.userRepository = userRepository;
		this.userAccounts = userAccounts;
	}

	public User createAccount(UserRegistrationForm form) {
		Assert.notNull(form, "Registration form must not be null!");

		var password = UnencryptedPassword.of(form.getPassword());
		var userAccount = userAccounts.create(form.getUsername(), password, USER_ROLE);

		userAccount.setFirstname(form.getFirstname());
		userAccount.setLastname(form.getLastname());

		return userRepository.save(new User(userAccount, form.getUsername()));
	}

	public Streamable<User> findAll() {
		return userRepository.findAll();
	}
}

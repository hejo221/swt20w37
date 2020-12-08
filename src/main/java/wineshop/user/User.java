package wineshop.user;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;


@Entity
public class User {

	@Id
	@GeneratedValue
	private long id;

	private String username;

	@OneToOne
	private UserAccount userAccount;

	@SuppressWarnings("unused")
	public User() {}

	public User(UserAccount userAccount, String username) {
		this.userAccount = userAccount;
		this.username = username;
	}


	public long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}
}

package wineshop.order;

import org.salespointframework.order.Cart;
import wineshop.customer.Customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;



@Entity
public class CartWithCustomer extends Cart {

	private @Id @GeneratedValue
	String id;

	@OneToOne
	private Customer customer;


	@SuppressWarnings("unused")
	public CartWithCustomer() {}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}





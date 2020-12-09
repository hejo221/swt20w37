package wineshop.order;
import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;
import wineshop.customer.Customer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
public class OrderCust extends Order {
	@OneToOne(cascade = {CascadeType.ALL})
	private Customer customer;


	public OrderCust(){}


	public OrderCust(UserAccount userAccount, Customer customer){
		super(userAccount);
		this.customer = customer;
	}

	public OrderCust(UserAccount userAccount, PaymentMethod paymentMethod, Customer customer){
		super(userAccount, paymentMethod);
		this.customer = customer;
	}

	public Customer getCustomer() {
		return customer;
	}
}

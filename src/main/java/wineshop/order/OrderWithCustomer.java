package wineshop.order;

import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;
import wineshop.customer.Customer;

public class OrderWithCustomer extends Order {
	private Customer customer;


	public OrderWithCustomer(UserAccount userAccount, Customer customer){
		super(userAccount);
		this.customer = customer;
	}

	public OrderWithCustomer(UserAccount userAccount, PaymentMethod paymentMethod, Customer customer){
		super(userAccount, paymentMethod);
		this.customer = customer;
	}
}

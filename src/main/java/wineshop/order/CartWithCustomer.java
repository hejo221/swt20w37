package wineshop.order;

import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import wineshop.customer.Customer;

import java.util.LinkedHashMap;
import java.util.Map;

public class CartWithCustomer extends Cart {
	private Customer customer;

	public Customer getCustomer() {
		return customer;
	}
}

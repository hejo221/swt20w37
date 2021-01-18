package wineshop.order;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import wineshop.customer.Customer;

import javax.persistence.*;
import java.util.Iterator;

@Entity
@Table(
		name = "ORDERCUST"
)
public class OrderCust extends Order {

	@OneToOne
	private Customer customer;

	private OrderType orderType;

	public OrderCust(){}


	public OrderCust(UserAccount userAccount, PaymentMethod paymentMethod, OrderType orderType) {
		super(userAccount, paymentMethod);
		if(orderType == null){
			throw new NullPointerException();
		}
		this.orderType = orderType;
	}

	public OrderCust(UserAccount userAccount, PaymentMethod paymentMethod, Customer customer, OrderType orderType) {
		super(userAccount, paymentMethod);
		if(customer == null || orderType == null){
			throw new NullPointerException();
		}
		this.customer = customer;
		this.orderType = orderType;
	}

	public Customer getCustomer() {
		return customer;
	}

	public String getPaymentMethodString(){
		System.out.println(this.getPaymentMethod().toString());
		if (this.getPaymentMethod().toString().equals("Cash()")) {
			return "Bargeld";
		}
		else return "Kreditkarte";
	}
	public OrderType getOrderType() {return this.orderType;}

	public void setOrderType(OrderType orderType) {this.orderType = orderType;}

	public boolean isOrder() {
		if (orderType == OrderType.ORDER) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isPreorder() {
		if (orderType == OrderType.PREORDER) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isReorder() {
		if (orderType == OrderType.REORDER) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isCloseable(UniqueInventory<UniqueInventoryItem> inventory) {
		Iterator<OrderLine> orderLineIterator= getOrderLines().iterator();

		do {
			OrderLine orderLine = orderLineIterator.next();
			UniqueInventoryItem item = inventory.findByProductIdentifier(orderLine.getProductIdentifier()).get();
			if (item.getQuantity().isGreaterThanOrEqualTo(orderLine.getQuantity()) ) {
				continue;
			} else {
				return false;
			}
		} while (orderLineIterator.hasNext());

		return true;
	}
}

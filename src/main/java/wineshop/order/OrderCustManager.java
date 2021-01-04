package wineshop.order;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import wineshop.customer.Customer;
import wineshop.customer.CustomerManager;
import wineshop.wine.Wine;

import java.util.Iterator;


@Service
@Transactional
public class OrderCustManager{
	private final OrderManagement<OrderCust> orderManagement;
	private final CustomerManager customerManager;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	private static final Logger LOG = LoggerFactory.getLogger(OrderCustManager.class);


	OrderCustManager(OrderManagement<OrderCust> orderManagement, CustomerManager customerManager, UniqueInventory<UniqueInventoryItem> inventory) {
		this.orderManagement = orderManagement;
		this.customerManager = customerManager;
		this.inventory = inventory;
	}

	
	public void cartToOrderAndPreOrder(UserAccount userAccount, Cart cart, CartCustForm cartCustForm){
		Customer customer = customerManager.findCustomerById((Long) cartCustForm.getCustomerId());
		var preorder = new OrderCust(userAccount, Cash.CASH, customer, OrderType.PREORDER);
		var order = new OrderCust(userAccount, Cash.CASH, customer, OrderType.ORDER);

		Iterator<CartItem> item = cart.iterator();
		do {
			CartItem cartItem = item.next();
			Wine wine = (Wine) cartItem.getProduct();
			InventoryItem inventoryItem = inventory.findByProductIdentifier(wine.getId()).get();

			if (cartItem.getQuantity().isGreaterThan(inventoryItem.getQuantity())) {
				preorder.addOrderLine(wine, cartItem.getQuantity());
				//inventoryItem.increaseQuantity((Quantity.of(10).subtract(inventoryItem.getQuantity())).add(cartItem.getQuantity()));
			} else {
				order.addOrderLine(wine, cartItem.getQuantity());
			}
		} while (item.hasNext());

		cart.clear();

		if (order.getOrderLines().stream().count() != 0) {
			Iterator<OrderLine> orderLineIterator = order.getOrderLines().get().iterator();
			do {
				OrderLine orderLine = orderLineIterator.next();
				Wine wine = (Wine) inventory.findByProductIdentifier(orderLine.getProductIdentifier()).get().getProduct();
				Quantity quantity = orderLine.getQuantity();

				cart.addOrUpdateItem(wine, quantity);
			} while (orderLineIterator.hasNext());
		}

		orderManagement.payOrder(order);
		orderManagement.completeOrder(order);
		orderManagement.save(preorder);
	}



	public void refresh(String itemId, int number, Cart cart) {

		CartItem item = cart.getItem(itemId).get();
		Wine wine = (Wine) item.getProduct();

		cart.addOrUpdateItem(wine, Quantity.of(0).subtract(item.getQuantity()));
		cart.addOrUpdateItem(wine, Quantity.of(number));
	}

	public void deleteItem(String itemId, Cart cart) {
		cart.removeItem(itemId);
	}

}

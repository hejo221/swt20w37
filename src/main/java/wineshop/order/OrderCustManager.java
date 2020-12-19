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
	private final OrderCustRepository orderCustRepository;
	private final OrderManagement<OrderCust> orderManagement;
	private final CustomerManager customerManager;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	private static final Logger LOG = LoggerFactory.getLogger(OrderCustManager.class);


	OrderCustManager(OrderCustRepository orderCustRepository, OrderManagement<OrderCust> orderManagement, CustomerManager customerManager, UniqueInventory<UniqueInventoryItem> inventory) {
		this.orderManagement = orderManagement;
		this.customerManager = customerManager;
		this.inventory = inventory;
		Assert.notNull(orderCustRepository, "OrderCustRepository must not be null!");

		this.orderCustRepository = orderCustRepository;
	}

	
	public void cartToOrderAndPreOrder(UserAccount userAccount, Cart cart, CartCustForm cartCustForm){
		if(!cart.isEmpty()){
			Customer customer = customerManager.findCustomerById((Long) cartCustForm.getCustomerId());
			var order = new OrderCust(userAccount, Cash.CASH, customer, OrderType.ORDER);
			var preorder = new OrderCust(userAccount, Cash.CASH, customer, OrderType.PREORDER);
			//TODO: NEU
			// 		Nachbestellungen: 	die Quantity von CartItem und IventoryItem wird verglichen,
			//							ist die Quantity kleiner, wird das CartItem in die Preorder
			//							überführt, ansonsten in die Order
			Iterator<CartItem> item = cart.iterator();
			do {
				CartItem cartItem = item.next();
				Wine wine = (Wine) cartItem.getProduct();
				InventoryItem inventoryItem = inventory.findByProductIdentifier(wine.getId()).get();

				if (cartItem.getQuantity().isGreaterThan(inventoryItem.getQuantity())) {
					preorder.addOrderLine(wine, cartItem.getQuantity());
					//TODO: Hier findet das "Nachbestellen" statt
					inventoryItem.increaseQuantity((Quantity.of(10).subtract(inventoryItem.getQuantity())).add(cartItem.getQuantity()));
					//preorder.reorder(inventoryItem);
				} else {
					order.addOrderLine(wine, cartItem.getQuantity());;
				}
			}
			while (item.hasNext());
			//TODO:	Falls eine Order leer sein sollte, wird diese gelöscht.
			//		Ansonsten wird sie bezahlt und anschließend geschlossen.

			if (preorder.getOrderLines().get().count() == 0) {
				orderManagement.delete(preorder);
			} else {
				orderManagement.payOrder(preorder);
				orderManagement.completeOrder(preorder);
			}
			if (order.getOrderLines().get().count() == 0) {
				orderManagement.delete(order);
			} else {
				orderManagement.payOrder(order);
				orderManagement.completeOrder(order);
			}
		}
	}

	public void orderCheckout(UserAccount userAccount, Cart cart, CartCustForm cartCustForm) {
		Customer customer = customerManager.findCustomerById((Long) cartCustForm.getCustomerId());
		var order = new OrderCust(userAccount, Cash.CASH, customer, OrderType.ORDER);
		var preorder = new OrderCust(userAccount, Cash.CASH, customer, OrderType.PREORDER);

		Iterator<CartItem> item = cart.iterator();
		do {
			CartItem cartItem = item.next();
			Wine wine = (Wine) cartItem.getProduct();
			InventoryItem inventoryItem = inventory.findByProductIdentifier(wine.getId()).get();

			if (!cartItem.getQuantity().isGreaterThan(inventoryItem.getQuantity())) {
				order.addOrderLine(wine, cartItem.getQuantity());
			} else {
				preorder.addOrderLine(wine, cartItem.getQuantity());
			}
		} while (item.hasNext());

		cart.clear();

		if (preorder.getOrderLines().stream().count() != 0) {
			Iterator<OrderLine> orderLineIterator = preorder.getOrderLines().get().iterator();
			do {
				OrderLine orderLine = orderLineIterator.next();
				Wine wine = (Wine) inventory.findByProductIdentifier(orderLine.getProductIdentifier()).get().getProduct();
				Quantity quantity = orderLine.getQuantity();

				cart.addOrUpdateItem(wine, quantity);
			} while (orderLineIterator.hasNext());
		}

		orderManagement.delete(preorder);
		orderManagement.payOrder(order);
		orderManagement.completeOrder(order);


	}

	public void preorderCheckout(UserAccount userAccount, Cart cart, CartCustForm cartCustForm) {
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

		orderManagement.delete(order);
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

	public Streamable<OrderCust> findAll() {
		return orderCustRepository.findAll();
	}

	public OrderCust findOrderCustById(Long id){
		return orderCustRepository.findById(id).get();
	}
}

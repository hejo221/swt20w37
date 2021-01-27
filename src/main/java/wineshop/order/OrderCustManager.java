package wineshop.order;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.CreditCard;
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
import wineshop.inventory.InventoryManager;
import wineshop.wine.Wine;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;


@Service
@Transactional
public class OrderCustManager {
	private final OrderManagement<OrderCust> orderManagement;
	private final CustomerManager customerManager;
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final ReorderManager reorderManager;

	private static final Logger LOG = LoggerFactory.getLogger(OrderCustManager.class);


	OrderCustManager(OrderManagement<OrderCust> orderManagement, CustomerManager customerManager, UniqueInventory<UniqueInventoryItem> inventory, ReorderManager reorderManager, InventoryManager inventoryManager) {
		this.orderManagement = orderManagement;
		this.customerManager = customerManager;
		this.inventory = inventory;
		this.reorderManager = reorderManager;
		this.inventoryManager = inventoryManager;
	}


	public void cartToOrderAndPreOrder(UserAccount userAccount, Cart cart, CartCustForm cartCustForm) {
		Customer customer = customerManager.findCustomerById((Long) cartCustForm.getCustomerId());
		CreditCard creditCard = new CreditCard("a", "b", "c", "d", "e", LocalDateTime.of(2020, 12, 31, 22, 33), LocalDateTime.of(2020, 12, 31, 22, 33), "9", Money.of(500, "EUR"), Money.of(500, "EUR"));
		var preorder = new OrderCust(userAccount, Cash.CASH, customer, OrderType.PREORDER);
		OrderCust order;
		if (cartCustForm.getPaymentMethod().equals("Bargeld")) {
			order = new OrderCust(userAccount, Cash.CASH, customer, OrderType.ORDER);
		} else {
			order = new OrderCust(userAccount, creditCard, customer, OrderType.ORDER);
		}

		Iterator<CartItem> item = cart.iterator();
		do {
			CartItem cartItem = item.next();
			Wine wine = (Wine) cartItem.getProduct();
			UniqueInventoryItem inventoryItem = inventory.findByProductIdentifier(wine.getId()).get();
			Wine inventoryWine = (Wine) inventory.findByProductIdentifier(wine.getId()).get().getProduct();
			Quantity inventoryQuantity = inventory.findByProductIdentifier(wine.getId()).get().getQuantity();

			if (cartItem.getQuantity().isGreaterThan(inventoryItem.getQuantity())) {
				if (inventoryItem.getQuantity().subtract(cartItem.getQuantity()).isLessThan(inventoryWine.getMinAmount())) {
					Iterator<OrderCust> reorderIterator = orderManagement.findBy(OrderStatus.OPEN).iterator();
					if (reorderIterator.hasNext()) {
						do {
							OrderCust reorder = reorderIterator.next();
							if (reorder.isReorder() && reorder.getOrderLines().iterator().next().getProductName().equals(cartItem.getProductName())) {
								break;
							} else if (reorder.isReorder() && !reorder.getOrderLines().iterator().next().getProductName().equals(cartItem.getProductName()) && !reorderIterator.hasNext()) {
								reorderManager.reorderWine(wine.getId(), Quantity.of(inventoryWine.getMaxAmount()).subtract(inventoryItem.getQuantity().subtract(cartItem.getQuantity())).getAmount().intValue(), userAccount);
							} else if (!reorder.isReorder() && !reorderIterator.hasNext()) {
								reorderManager.reorderWine(wine.getId(), Quantity.of(inventoryWine.getMaxAmount()).subtract(inventoryItem.getQuantity().subtract(cartItem.getQuantity())).getAmount().intValue(), userAccount);
							}
						} while (reorderIterator.hasNext());
					} else {
						reorderManager.reorderWine(wine.getId(), Quantity.of(inventoryWine.getMaxAmount()).subtract(inventoryItem.getQuantity().subtract(cartItem.getQuantity())).getAmount().intValue(), userAccount);
					}
				}


				preorder.addOrderLine(wine, cartItem.getQuantity().subtract(inventoryItem.getQuantity()));
				if (!inventoryItem.getQuantity().isZeroOrNegative()) {
					order.addOrderLine(wine, inventoryItem.getQuantity());
				}

				//inventoryItem.increaseQuantity((Quantity.of(10).subtract(inventoryItem.getQuantity())).add(cartItem.getQuantity()));
			} else {
				order.addOrderLine(wine, cartItem.getQuantity());
				if (inventoryItem.getQuantity().subtract(cartItem.getQuantity()).isLessThan(inventoryWine.getMinAmount())) {
					Iterator<OrderCust> reorderIterator = orderManagement.findBy(OrderStatus.OPEN).iterator();
					if (reorderIterator.hasNext()) {
						do {
							OrderCust reorder = reorderIterator.next();
							if (reorder.isReorder() && reorder.getOrderLines().iterator().next().getProductName().equals(cartItem.getProductName())) {
								break;
							} else if (reorder.isReorder() && !reorder.getOrderLines().iterator().next().getProductName().equals(cartItem.getProductName()) && !reorderIterator.hasNext()) {
								reorderManager.reorderWine(wine.getId(), Quantity.of(inventoryWine.getMaxAmount()).subtract(inventoryItem.getQuantity().subtract(cartItem.getQuantity())).getAmount().intValue(), userAccount);
							} else if (!reorder.isReorder() && !reorderIterator.hasNext()) {
								reorderManager.reorderWine(wine.getId(), Quantity.of(inventoryWine.getMaxAmount()).subtract(inventoryItem.getQuantity().subtract(cartItem.getQuantity())).getAmount().intValue(), userAccount);
							}
						} while (reorderIterator.hasNext());
					} else {
						reorderManager.reorderWine(wine.getId(), Quantity.of(inventoryWine.getMaxAmount()).subtract(inventoryItem.getQuantity().subtract(cartItem.getQuantity())).getAmount().intValue(), userAccount);
					}
				}
			}

		} while (item.hasNext());

		cart.clear();

		if (order.getOrderLines().isEmpty()) {
			orderManagement.delete(order);
		} else {
			orderManagement.payOrder(order);
			orderManagement.completeOrder(order);
		}
		if (preorder.getOrderLines().isEmpty()) {
			orderManagement.delete(preorder);
		} else {
			orderManagement.save(preorder);
		}
	}


	public void emptyBasket(Cart cart) {
		cart.clear();
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

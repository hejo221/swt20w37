package wineshop.order;


import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wineshop.inventory.InventoryManager;
import wineshop.wine.Wine;

import javax.transaction.Transactional;

@Service
@Transactional
public class ReorderManager {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final OrderManagement<OrderCust> orderManagement;

	private static final Logger LOG = LoggerFactory.getLogger(ReorderManager.class);

	ReorderManager(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager, OrderManagement<OrderCust> orderManagement) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
		this.orderManagement = orderManagement;
	}

	@Transactional
	void reorderWine(ProductIdentifier productId, int amount, UserAccount userAccount) {
		Wine wine = (Wine) inventory.findByProductIdentifier(productId).get().getProduct();
		Money savePrice = wine.getSellPrice();
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		var reorder = new OrderCust(userAccount, Cash.CASH, OrderType.REORDER);

		wine.setPrice(wine.getBuyPrice().negate());
		reorder.addOrderLine(wine, Quantity.of(amount));
		orderManagement.save(reorder);
		wine.setPrice(savePrice);
		inventory.save(item);
	}

	@Transactional
	void closeReorder(OrderIdentifier id) {
		OrderCust reorder = orderManagement.get(id).get();
		OrderLine orderLine = reorder.getOrderLines().iterator().next();
		UniqueInventoryItem item = inventory.findByProductIdentifier(orderLine.getProductIdentifier()).get();

		item.increaseQuantity(orderLine.getQuantity().times(2));
		inventory.save(item);
		orderManagement.payOrder(reorder);
		orderManagement.completeOrder(reorder);
	}
}

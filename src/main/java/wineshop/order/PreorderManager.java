package wineshop.order;

import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wineshop.email.EmailService;
import wineshop.inventory.InventoryManager;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Eine Klasse, welche wichtige Funktionen für die Verwaltung von Vorbestellungen zusammenfasst
 */
@Service
@Transactional
public class PreorderManager {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final OrderManagement<OrderCust> orderManagement;
	private final OrderCustManager orderCustManager;
	@Autowired
	private EmailService emailService;
	private static final Logger LOGGER = LoggerFactory.getLogger(PreorderManager.class);

	private static final Logger LOG = LoggerFactory.getLogger(PreorderManager.class);

	PreorderManager(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager, OrderManagement<OrderCust> orderManagement, OrderCustManager orderCustManager) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
		this.orderManagement = orderManagement;
		this.orderCustManager = orderCustManager;
	}

	/**
	 * Eine Vorbestellung wird reserviert
	 * Die in der Vorbestellung enthaltenen Weine werden aus dem Lager entfernt
	 *
	 * @param id Die Id der Vorbestellung
	 * @param userAccount Der angemeldete Mitarbeiter
	 */
	@Transactional
	int reservePreorder(OrderIdentifier id, UserAccount userAccount) {
		OrderCust preorder = orderManagement.get(id).get();
		UniqueInventoryItem item = inventory.findByProductIdentifier(preorder.getOrderLines().iterator().next().getProductIdentifier()).get();
		preorder.reserve();
		//Weine werden aus dem Lager herausgenommen und für diese Bestellung reserviert
		Iterator<OrderLine> orderLineIterator = preorder.getOrderLines().iterator();
		do {
			OrderLine orderLine = orderLineIterator.next();
			inventoryManager.decreaseAmount(orderLine.getProductIdentifier(), orderLine.getQuantity());
		} while (orderLineIterator.hasNext());
		orderManagement.save(preorder);

		// to send mail
		return inventoryManager.sendEmail(item);
	}
}

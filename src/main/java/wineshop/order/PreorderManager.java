package wineshop.order;

import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wineshop.inventory.InventoryManager;

import javax.transaction.Transactional;
import java.util.Iterator;

@Service
@Transactional
public class PreorderManager {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final OrderManagement<OrderCust> orderManagement;
	private final OrderCustManager orderCustManager;

	private static final Logger LOG = LoggerFactory.getLogger(PreorderManager.class);

	PreorderManager(UniqueInventory<UniqueInventoryItem> inventory, InventoryManager inventoryManager, OrderManagement<OrderCust> orderManagement, OrderCustManager orderCustManager) {
		this.inventory = inventory;
		this.inventoryManager = inventoryManager;
		this.orderManagement = orderManagement;
		this.orderCustManager = orderCustManager;
	}
}

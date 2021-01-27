package wineshop.order;


import com.google.common.collect.Lists;
import org.aspectj.weaver.ast.Or;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import wineshop.email.EmailService;
import wineshop.inventory.InventoryManager;
import wineshop.wine.Wine;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReorderManager {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final InventoryManager inventoryManager;
	private final OrderManagement<OrderCust> orderManagement;
	@Autowired
	private EmailService emailService;
	private static final Logger LOGGER = LoggerFactory.getLogger(ReorderController.class);

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

	int sendEmail(UniqueInventoryItem item) {
		int email_flag = 0; // if a mail is send, then it is 1
		Quantity item_quantity = item.getQuantity();

		List<OrderCust> preorders = orderManagement.findBy(OrderStatus.OPEN).toList();
		preorders = preorders.stream().filter((e) -> {
			return e.isPreorder();
		}).collect(Collectors.toList());
		preorders = preorders.stream().sorted(Comparator.comparing(OrderCust::getDateCreated)).collect(Collectors.toList());

		for(int i = 0; i < preorders.size(); i++) {
			OrderCust preorder = preorders.get(i);
			List<OrderLine> productList = preorder.getOrderLines().toList();

			for(int j = 0; j < productList.size(); j++) {
				OrderLine productLine = productList.get(j);

				if(item.getProduct().getName() == productLine.getProductName()
						&& productLine.getQuantity().isLessThan(item_quantity)) {

					item_quantity = item_quantity.subtract(productLine.getQuantity());
					System.out.println(item_quantity);
					email_flag = 1;

					String title = "Der von Ihnen bestellte Wein ist angekommen.";
					String text = "Der von Ihnen bestellte Wein ist angekommen. \nBitte kontaktieren Sie uns, wenn Sie Ihre Bestellung abschließen möchten.\n"
							+ "\nKundenname : " + preorder.getCustomer().getFirstName() + " " + preorder.getCustomer().getFamilyName()
							+ "\nBestellungsdatum : " + preorder.getDateCreated().getDayOfMonth() + "." + preorder.getDateCreated().getMonthValue() + "." + preorder.getDateCreated().getYear()
							+ "\nWeinname : " + productLine.getProductName()
							+ "\nMenge : " + productLine.getQuantity();

					try{
						emailService.sendMail(preorder.getCustomer().getEmail(), title, text);
					}catch (Exception e) {
						LOGGER.error("Email kann nicht gesenden werden.", e);
						email_flag = 2;
					}

				}
			}
		}
		return email_flag;
	}


	void reserveOrder(OrderIdentifier orderId) {
		OrderCust order = orderManagement.get(orderId).get();

		Iterator<OrderLine> orderLineIterator= order.getOrderLines().iterator();
		do {
			OrderLine orderLine = orderLineIterator.next();
			UniqueInventoryItem item = inventory.findByProductIdentifier(orderLine.getProductIdentifier()).get();
			ProductIdentifier productId = item.getProduct().getId();

			inventoryManager.decreaseAmount(productId, orderLine.getQuantity());
		} while (orderLineIterator.hasNext());

		order.reserve();
		orderManagement.save(order);
	}


	@Transactional
	int closeReorder(OrderIdentifier id) {

		OrderCust reorder = orderManagement.get(id).get();
		OrderLine orderLine = reorder.getOrderLines().iterator().next();
		UniqueInventoryItem item = inventory.findByProductIdentifier(orderLine.getProductIdentifier()).get();

		item.increaseQuantity(orderLine.getQuantity().times(2));
		inventory.save(item);
		orderManagement.payOrder(reorder);
		orderManagement.completeOrder(reorder);

		Iterator<OrderCust> openPreorderIterator = orderManagement.findBy(OrderStatus.OPEN).iterator();
		do {
			OrderCust openPreorder = openPreorderIterator.next();
			if (openPreorder.isPreorder()) {
				if (openPreorder.isCloseable(inventory)) {
					reserveOrder(openPreorder.getId());
				}
			} else {
				continue;
			}

		} while (openPreorderIterator.hasNext());
		// to send mail
		return sendEmail(item);
	}
}

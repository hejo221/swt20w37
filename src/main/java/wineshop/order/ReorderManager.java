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


/**
 * Eine Klasse, welche wichtige Funktionen für die Verwaltung von Nachbestellungen zusammenfasst
 */
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

	/**
	 * Ein Wein wird nachbestellt
	 *
	 * @param productId Die Id des Weines
	 * @param amount Die gewünschte Anzahl (veraltet)
	 * @param userAccount Der angemeldete Mitarbeiter
	 */
	@Transactional
	void reorderWine(ProductIdentifier productId, int amount, UserAccount userAccount) {
		Wine wine = (Wine) inventory.findByProductIdentifier(productId).get().getProduct();
		Money savePrice = wine.getSellPrice();
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		Quantity maxAmount = Quantity.of(wine.getMaxAmount());
		var reorder = new OrderCust(userAccount, Cash.CASH, OrderType.REORDER);

		wine.setPrice(wine.getBuyPrice().negate());
		reorder.addOrderLine(wine, maxAmount);
		orderManagement.save(reorder);
		wine.setPrice(savePrice);
		inventory.save(item);
	}

	/**
	 * Eine Email wird an den Kunden gesendet
	 *
	 * @param curItem Der derzeitig ausgewählte Lagergegenstand bzw. Wein
	 * @return int, bzw. ob eine email gesendet wurde
	 */
	int sendEmail(UniqueInventoryItem curItem) {
		int email_flag = 0; // if a mail is send, then it is 1
		Quantity addedQuantity = curItem.getQuantity();

		List<OrderCust> preorders = orderManagement.findBy(OrderStatus.OPEN).toList();
		preorders = preorders.stream().filter((e) -> {
			return e.isReserved();
		}).collect(Collectors.toList());
		preorders = preorders.stream().sorted(Comparator.comparing(OrderCust::getDateCreated)).collect(Collectors.toList());

		for(int i = 0; i < preorders.size(); i++) {
			OrderCust preorder = preorders.get(i);
			List<OrderLine> productList = preorder.getOrderLines().toList();

			String text_product = "";
			for (int j = 0; j < productList.size(); j++) {
				text_product = text_product + " - " + productList.get(j).getProductName() + ": " + productList.get(j).getQuantity() + "\n";
			}

			String title = "Der von Ihnen bestellte Wein ist angekommen.";
			String text = "Der von Ihnen bestellte Wein ist angekommen. \nBitte kontaktieren Sie uns, wenn Sie Ihre Bestellung abschließen möchten.\n"
					+ "\nKundenname : " + preorder.getCustomer().getFirstName() + " " + preorder.getCustomer().getFamilyName()
					+ "\nBestellungsdatum : " + preorder.getDateCreated().getDayOfMonth() + "." + preorder.getDateCreated().getMonthValue() + "." + preorder.getDateCreated().getYear()
					+ "\n\nBestellliste\n" + text_product;

			try {
				if (preorder.getEmailStatus() == false) {
					emailService.sendMail(preorder.getCustomer().getEmail(), title, text);
					preorder.setEmailStatus(true);
					orderManagement.save(preorder);
					email_flag = 1;
				}
			} catch (Exception e) {
				LOGGER.error("Email kann nicht gesenden werden.", e);
				email_flag = 2;
			}
		}

		return email_flag;
	}

	int oldSendEmail(UniqueInventoryItem curItem) {
		int email_flag = 0; // if a mail is send, then it is 1
		Quantity addedQuantity = curItem.getQuantity();

		List<OrderCust> preorders = orderManagement.findBy(OrderStatus.OPEN).toList();
		preorders = preorders.stream().filter((e) -> {
			return e.isPreorder();
		}).collect(Collectors.toList());
		preorders = preorders.stream().sorted(Comparator.comparing(OrderCust::getDateCreated)).collect(Collectors.toList());

		for(int i = 0; i < preorders.size(); i++) {
			OrderCust preorder = preorders.get(i);
			List<OrderLine> productList = preorder.getOrderLines().toList();
			int flag = 0;

			for(int j = 0; j < productList.size(); j++) {
				OrderLine productLine = productList.get(j);
				UniqueInventoryItem item = inventory.findByProductIdentifier(productLine.getProductIdentifier()).get();

				if(productLine.getQuantity().isGreaterThan(item.getQuantity())) {
					flag = -1;
				}
				if(productLine.getQuantity().isGreaterThan(addedQuantity)) {
					flag = -1;
				}
			}

			if(flag == 0)  {
				for(int j = 0; j < productList.size(); j++) {
					if(productList.get(j).getProductName() == curItem.getProduct().getName()) {
						addedQuantity =	addedQuantity.subtract(productList.get(j).getQuantity());
					}
				}

				String text_product = "";
				for (int j = 0; j < productList.size(); j++) {
					text_product = text_product + " - " + productList.get(j).getProductName() + ": " + productList.get(j).getQuantity() + "\n";
				}

				String title = "Der von Ihnen bestellte Wein ist angekommen.";
				String text = "Der von Ihnen bestellte Wein ist angekommen. \nBitte kontaktieren Sie uns, wenn Sie Ihre Bestellung abschließen möchten.\n"
						+ "\nKundenname : " + preorder.getCustomer().getFirstName() + " " + preorder.getCustomer().getFamilyName()
						+ "\nBestellungsdatum : " + preorder.getDateCreated().getDayOfMonth() + "." + preorder.getDateCreated().getMonthValue() + "." + preorder.getDateCreated().getYear()
						+ "\n\nBestellliste\n" + text_product;

				try{
					if(preorder.getEmailStatus() == false) {
						emailService.sendMail(preorder.getCustomer().getEmail(), title, text);
						preorder.setEmailStatus(true);
						orderManagement.save(preorder);
						email_flag = 1;
					}
				}catch (Exception e) {
					LOGGER.error("Email kann nicht gesenden werden.", e);
					email_flag = 2;
				}
			}
		}
		return email_flag;
	}

	/**
	 * Eine offene Vorbestellung wird reserviert
	 * Wird in closeReorder() genutzt, um geeignete Vorbestellungen zu reservieren
	 *
	 * @param orderId Die Id der Vorbestellung
	 */
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

	/**
	 * Die Nachbestellung wird geschlossen
	 * Nach dem Schließen der Nachbestellung werden geeignete offene Vorbestellungen automatisch reserviert
	 *
	 * @param id Die Id der Nachbestellung
	 */
	@Transactional
	int closeReorder(OrderIdentifier id) {

		OrderCust reorder = orderManagement.get(id).get();
		OrderLine orderLine = reorder.getOrderLines().iterator().next();
		UniqueInventoryItem item = inventory.findByProductIdentifier(orderLine.getProductIdentifier()).get();

		item.increaseQuantity(orderLine.getQuantity().times(2));
		inventory.save(item);
		orderManagement.payOrder(reorder);
		orderManagement.completeOrder(reorder);

		//Geeignete Vorbestellungen werden automatisch reserviert
		Iterator<OrderCust> openPreorderIterator = orderManagement.findBy(OrderStatus.OPEN).iterator();
		do {
			if (openPreorderIterator.hasNext()) {
				OrderCust openPreorder = openPreorderIterator.next();
				if (openPreorder.isPreorder()) {
					if (openPreorder.isCloseable(inventory)) {
						if (!openPreorder.isReserved()) {
							reserveOrder(openPreorder.getId());
							System.out.println(openPreorder.isReserved());
						}
					}
				} else {
					continue;
				}
			}
		} while (openPreorderIterator.hasNext());

		// to send mail
		return inventoryManager.sendEmail(item);
	}
}

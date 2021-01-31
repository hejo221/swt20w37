package wineshop.inventory;


import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wineshop.email.EmailService;
import wineshop.order.OrderCust;
import wineshop.order.ReorderController;
import wineshop.wine.Wine;
import wineshop.wine.WineCatalog;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Eine Klasse, welche die für das Lager benötigten Hauptfunktionen zusammenfasst
 */
@Service
@Transactional
public class InventoryManager {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final OrderManagement<OrderCust> orderManagement;
	private final WineCatalog wineCatalog;
	@Autowired
	private EmailService emailService;
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryManager.class);

	InventoryManager(UniqueInventory<UniqueInventoryItem> inventory, OrderManagement<OrderCust> orderManagement, WineCatalog wineCatalog) {
		Assert.notNull(inventory, "Inventory darf nicht leer sein");
		this.inventory = inventory;
		this.orderManagement = orderManagement;
		this.wineCatalog = wineCatalog;
	}


	/**
	 * Der Lagerbestand eines Weines wird aktualisiert
	 *
	 * @param productId Die Id des Weines
	 * @param number der neue Lagerbestand
	 */
	@Transactional
	void updateAmount(ProductIdentifier productId, int number) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		item.decreaseQuantity(item.getQuantity());
		item.increaseQuantity(Quantity.of(number));
		inventory.save(item);
	}

	/**
	 * Funktion zum einfachen Erhöhen des Lagerbestandes
	 *
	 * @param productId Die Id des Weines
	 * @param quantity Die Quantity, welche dem Lagerbestand hinzugefügt wird
	 */
	public void increaseAmount(ProductIdentifier productId, Quantity quantity) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		item.increaseQuantity(quantity);
		inventory.save(item);
	}

	/**
	 * Funktion zum einfachen Senken des Lagerbestandes
	 *
	 * @param productId Die Id des Weines
	 * @param quantity Die Quantity, welche vom Lagerbestand abgezogen wird
	 */
	public void decreaseAmount(ProductIdentifier productId, Quantity quantity) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		item.decreaseQuantity(quantity);
		inventory.save(item);
	}

	/**
	 * Der Minimalbestand wird aktualisiert
	 * Fällt der Lagerbestand unter diesen Bestand, können Nachbestellungen automatisch aufgegeben werden
	 *
	 * @param productId Die Id des Weines
	 * @param number Der neue Minimalbestand
	 */
	@Transactional
	void updateMinAmount(ProductIdentifier productId, int number) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		Wine wine = (Wine) item.getProduct();
		wine.setMinAmount(Quantity.of(number));
		inventory.save(item);
	}

	/**
	 * Der Maximalbestand wird aktualisiert
	 * Der Maximalbestand legt fest, wie viele Weine in einer automatischen Nachbestellung bestellt werden
	 * @param productId Die Id des Weines
	 * @param number Der neue Maximalbestand
	 */
	@Transactional
	void updateMaxAmount(ProductIdentifier productId, int number) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		Wine wine = (Wine) item.getProduct();
		wine.setMaxAmount(number);
		inventory.save(item);
	}

	/**
	 * Ein Wein wird aus dem Lager entfernt
	 *
	 * @param productId Die Id des Weines
	 */
	void deleteItem(ProductIdentifier productId) {
		String wineName = inventory.findByProductIdentifier(productId).get().getProduct().getName();
		Wine wine = wineCatalog.findByName(wineName).iterator().next();

		inventory.delete(inventory.findByProduct(wine).get());
		wineCatalog.deleteById(wine.getId());
	}

	/**
	 * Eine Email wird an den Kunden gesendet
	 *
	 * @param curItem Der derzeitig ausgewählte Lagergegenstand bzw. Wein
	 * @return int, bzw. ob eine email gesendet wurde
	 */
	public int sendEmail(UniqueInventoryItem curItem) {

		int email_flag = 0; // if a mail is send, then it is 1
		Quantity addedQuantity = curItem.getQuantity();

		List<OrderCust> preorders = orderManagement.findBy(OrderStatus.OPEN).toList();
		preorders = preorders.stream().filter(OrderCust::isReserved).collect(Collectors.toList());
		preorders = preorders.stream().sorted(Comparator.comparing(OrderCust::getDateCreated)).collect(Collectors.toList());

		for (OrderCust preorder : preorders) {
			List<OrderLine> productList = preorder.getOrderLines().toList();

			String text_product = "";
			for (OrderLine orderLine : productList) {
				text_product = text_product + " - " + orderLine.getProductName() + ": " + orderLine.getQuantity() + "\n";
			}

			String title = "Der von Ihnen bestellte Wein ist angekommen.";
			String text = "Der von Ihnen bestellte Wein ist angekommen. \nBitte kontaktieren Sie uns, wenn Sie Ihre Bestellung abschließen möchten.\n"
					+ "\nKundenname : " + preorder.getCustomer().getFirstName() + " " + preorder.getCustomer().getFamilyName()
					+ "\nBestellungsdatum : " + preorder.getDateCreated().getDayOfMonth() + "." + preorder.getDateCreated().getMonthValue() + "." + preorder.getDateCreated().getYear()
					+ "\n\nBestellliste\n" + text_product;

			try {
				if (!preorder.getEmailStatus()) {
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

}
package wineshop.inventory;


import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wineshop.wine.Wine;

import javax.transaction.Transactional;

/**
 * Eine Klasse, welche die für das Lager benötigten Hauptfunktionen zusammenfasst
 */
@Service
@Transactional
public class InventoryManager {

	private final UniqueInventory<UniqueInventoryItem> inventory;

	InventoryManager(UniqueInventory<UniqueInventoryItem> inventory) {
		Assert.notNull(inventory, "Inventory darf nicht leer sein");
		this.inventory = inventory;
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
		inventory.delete(inventory.findByProductIdentifier(productId).get());
	}

}
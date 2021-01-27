package wineshop.inventory;


import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wineshop.wine.Wine;

import javax.transaction.Transactional;

@Service
@Transactional
public class InventoryManager {

	private final UniqueInventory<UniqueInventoryItem> inventory;

	InventoryManager(UniqueInventory<UniqueInventoryItem> inventory) {
		Assert.notNull(inventory, "Inventory darf nicht leer sein");
		this.inventory = inventory;
	}

	@Transactional
	void updateAmount(ProductIdentifier productId, int number) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		item.decreaseQuantity(item.getQuantity());
		item.increaseQuantity(Quantity.of(number));
		inventory.save(item);
	}

	public void increaseAmount(ProductIdentifier productId, Quantity quantity) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		item.increaseQuantity(quantity);
		inventory.save(item);
	}

	public void decreaseAmount(ProductIdentifier productId, Quantity quantity) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		item.decreaseQuantity(quantity);
		inventory.save(item);
	}

	@Transactional
	void updateMinAmount(ProductIdentifier productId, int number) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		Wine wine = (Wine) item.getProduct();
		wine.setMinAmount(Quantity.of(number));
		inventory.save(item);
	}

	@Transactional
	void updateMaxAmount(ProductIdentifier productId, int number) {
		UniqueInventoryItem item = inventory.findByProductIdentifier(productId).get();
		Wine wine = (Wine) item.getProduct();
		wine.setMaxAmount(number);
		inventory.save(item);
	}

	void deleteItem(ProductIdentifier productId) {
		inventory.delete(inventory.findByProductIdentifier(productId).get());
	}

}
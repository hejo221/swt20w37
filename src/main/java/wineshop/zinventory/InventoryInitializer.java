package wineshop.zinventory;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import wineshop.wine.WineCatalog;

@Component
@Order(20)
class InventoryInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(InventoryInitializer.class);

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final WineCatalog wineCatalog;

	InventoryInitializer(UniqueInventory<UniqueInventoryItem> inventory, WineCatalog wineCatalog) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(wineCatalog, "WineRepository must not be null!");

		this.inventory = inventory;
		this.wineCatalog = wineCatalog;
	}

	@Override
	public void initialize() {

		LOG.info("Creating inventory items.");

		wineCatalog.findAll().forEach(wine -> {
			// Try to find an InventoryItem for the project and create a default one with 10 items if none available
			if (inventory.findByProduct(wine).isEmpty()) {
				inventory.save(new UniqueInventoryItem(wine, Quantity.of(10)));
			}
		});
	}

}
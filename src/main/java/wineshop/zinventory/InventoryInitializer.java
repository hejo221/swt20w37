package wineshop.zinventory;

import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import wineshop.wine.WineRepository;

@Component
@Order(20)
class InventoryInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(InventoryInitializer.class);

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final WineRepository wineRepository;

	InventoryInitializer(UniqueInventory<UniqueInventoryItem> inventory, WineRepository wineRepository) {

		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(wineRepository, "WineRepository must not be null!");

		this.inventory = inventory;
		this.wineRepository = wineRepository;
	}

	@Override
	public void initialize() {

		LOG.info("Creating inventory items.");

		wineRepository.findAll().forEach(wine -> {
			// Try to find an InventoryItem for the project and create a default one with 10 items if none available
			if (inventory.findByProduct(wine).isEmpty()) {
				inventory.save(new UniqueInventoryItem(wine, Quantity.of(10)));
			}
		});
	}

}

package wineshop.inventory;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import wineshop.AbstractIntegrationTests;
import wineshop.inventory.InventoryManager;
import wineshop.wine.CatalogManager;
import wineshop.wine.Wine;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


public class InventoryTests extends AbstractIntegrationTests {

	@Autowired
	UniqueInventory<UniqueInventoryItem> inventory;
	@Autowired
	InventoryManager inventoryManager;
	@Autowired
	InventoryController controller;
	@Autowired
	CatalogManager catalogManager;

	// Hier werden alle Methoden der Klasse InventoryController getestet
	@Test
	@WithMockUser(roles = "ADMIN")
	public void sampleControllerIntegrationTest() {
		Optional<String> none = Optional.empty();
		Model model = new ExtendedModelMap();

		String returnedView = controller.stock(model);
		assertThat(returnedView).isEqualTo("inventory/inventory");

		returnedView = controller.updateAmount(catalogManager.getAllWines().toList().get(0).getProductId(), 10);
		assertThat(returnedView).isEqualTo("redirect:/inventory");

		returnedView = controller.deleteItem(catalogManager.getAllWines().toList().get(0).getProductId());
		assertThat(returnedView).isEqualTo("redirect:/inventory");

		returnedView = controller.updateMinAmount(inventory.findAll().toList().get(0).getProduct().getId(), 10);
		assertThat(returnedView).isEqualTo("redirect:/inventory");
	}


	// InventoryManager wird getestet
	@Test
	void throwsEmptyInventory() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new InventoryManager(null));
	}


	// Bestandänderung
	@Test
	public void updatesAmountOfEachProduct() {
		for (Wine wine : catalogManager.getAllWines()){
			controller.updateAmount(wine.getProductId(), 333);
		}

		for (Wine wine : catalogManager.getAllWines()){
			assertThat(inventory.findByProductIdentifier(wine.getProductId()).get().getQuantity()).isEqualTo(Quantity.of(333));
		}
	}

	// MIN-Bestandänderung
	@Test
	public void updatesMinAmountOfEachProduct() {
		for (Wine wine : catalogManager.getAllWines()){
			controller.updateMinAmount(wine.getProductId(), 111);
		}

		for (Wine wine : catalogManager.getAllWines()){
			assertThat(wine.getMinAmount()).isEqualTo(Quantity.of(111));
		}
	}

	// Deletes all Items in Inventory
	@Test
	public void deletesItems(){
		for (UniqueInventoryItem item : inventory.findAll()){
			inventoryManager.deleteItem(item.getProduct().getId());
		}
		assertThat(inventory.findAll()).hasSize(0);
	}
}

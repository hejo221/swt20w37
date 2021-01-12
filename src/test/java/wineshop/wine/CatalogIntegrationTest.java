package wineshop.wine;

import org.junit.jupiter.api.Test;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import wineshop.AbstractIntegrationTests;

import static org.assertj.core.api.Assertions.*;

public class CatalogIntegrationTest extends AbstractIntegrationTests {

	@Autowired
	CatalogManager catalogManager;
	@Autowired
	UniqueInventory<UniqueInventoryItem> inventory;

	// Anzahl aller Weinprodukte im Katalog
	@Test
	public void findsAllWines() {
		Iterable<Wine> allWines = catalogManager.getAllWines();
		assertThat(allWines).hasSize(14);
	}

	// Anzahl aller verfügbaren Produkten im Katalog
	@Test
	public void findsAllAvailableWines() {
		Iterable<Wine> availableWines = catalogManager.getAvailableWines();
		assertThat(availableWines).hasSize(9);
	}

	// Anzahl aller nicht verfügbaren Produkten im Katalog
	@Test
	public void findsAllUnavailableWines() {
		Iterable<Wine> unavailableWines = catalogManager.getUnavailableWines();
		assertThat(unavailableWines).hasSize(5);
	}

	// Prüft die Methode isAvailable() und isUnavailable
	@Test
	public void testsIsAvailableOrNot(){
		int availableCounter = 0;
		int unavailableCounter = 0;
		int errorCounter = 0;
		for (Wine wine : catalogManager.getAllWines()){
			if(catalogManager.isAvailable(wine)) availableCounter ++;
			else if (catalogManager.isUnavailable(wine)) unavailableCounter ++;
			else errorCounter++;
		}
		assertThat(availableCounter).isEqualTo(9);
		assertThat(unavailableCounter).isEqualTo(5);
		assertThat(errorCounter).isEqualTo(0);

	}

	// Macht alle verfügbare Weine nicht verfügbar
	@Test
	public void doesFromAllAvailableUnavailable() {
		for (Wine wine : catalogManager.getAvailableWines())
			catalogManager.makeItemUnavailable(wine);

		assertThat(catalogManager.getAllWines()).hasSize(14);
		assertThat(catalogManager.getAvailableWines()).hasSize(0);
		assertThat(catalogManager.getUnavailableWines()).hasSize(14);
	}

	// Macht alle nicht verfügbare Weine (wieder) verfügbar
	@Test
	public void doesFromAllUnavailableAvailable() {
		for (Wine wine : catalogManager.getUnavailableWines())
			catalogManager.makeItemAvailable(wine);

		assertThat(catalogManager.getAllWines()).hasSize(14);
		assertThat(catalogManager.getAvailableWines()).hasSize(14);
		assertThat(catalogManager.getUnavailableWines()).hasSize(0);
	}

	// Löscht komplett alle verfügbare Weinprodukte
	@Test
	public void deletesAllAvailableWines(){
		Iterable<Wine> availableWines = catalogManager.getAvailableWines();
		for (Wine wine : availableWines){
			if (inventory.findByProduct(wine).isPresent()) {
				inventory.delete(inventory.findByProduct(wine).get());
			}
			catalogManager.deleteById(wine.getProductId());
		}

		assertThat(catalogManager.getAllWines()).hasSize(5);
		assertThat(catalogManager.getAvailableWines()).hasSize(0);
		assertThat(catalogManager.getUnavailableWines()).hasSize(5);
	}

	// Hinzufügen von neuen Weinen
}

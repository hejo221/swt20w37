package wineshop.wine;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import wineshop.AbstractIntegrationTests;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;


public class CatalogTests extends AbstractIntegrationTests {

	@Autowired
	CatalogManager catalogManager;
	@Autowired
	UniqueInventory<UniqueInventoryItem> inventory;
	@Autowired
	CatalogController controller;

	NewProductForm form1 = new NewProductForm
			("12345", "TestWein", Wine.WineType.ROSE, "no", "444", "555", "no comment");
	NewProductForm form2 = new NewProductForm
			("54321", "KeinTest", Wine.WineType.OTHER, "yes", "222", "333", "Kein Kommentar");


	// Hier werden alle Methoden der Klasse CatalogController getestet
	@Test
	@WithMockUser(roles = "ADMIN")
	public void sampleControllerIntegrationTest() {
		Optional<String> none = Optional.empty();
		Model model = new ExtendedModelMap();
		Errors errors = new BeanPropertyBindingResult(form1, "objectName");

		String returnedView = controller.showAvailableWines(model, none, none, none);
		assertThat(returnedView).isEqualTo("wine/catalog");

		returnedView = controller.showUnavailableWines(model);
		assertThat(returnedView).isEqualTo("wine/unavailableProducts");

		returnedView = controller.register(model, form1);
		assertThat(returnedView).isEqualTo("wine/newProduct");

		returnedView = controller.registerNew(form1, errors);
		assertThat(returnedView).isEqualTo("redirect:/catalog");

		returnedView = controller.showProduct(catalogManager.getAllWines().toList().get(0), model,  form1);
		assertThat(returnedView).isEqualTo("wine/product");

		returnedView = controller.productEdit(catalogManager.getAllWines().toList().get(0), form1, errors);
		assertThat(returnedView).isEqualTo("redirect:/catalog");

		returnedView = controller.deleteConfirm(catalogManager.getAllWines().toList().get(0), model);
		assertThat(returnedView).isEqualTo("wine/deleteConfirmation");

		returnedView = controller.deleteItem(catalogManager.getAllWines().toList().get(0));
		assertThat(returnedView).isEqualTo("redirect:/catalog");

		returnedView = controller.recoverItem(catalogManager.getAllWines().toList().get(0));
		assertThat(returnedView).isEqualTo("redirect:/catalog");

		returnedView = controller.deleteItem(catalogManager.getAllWines().toList().get(0));
		assertThat(returnedView).isEqualTo("redirect:/catalog");
	}


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
	public void testsIsAvailableOrNot() {
		int availableCounter = 0;
		int unavailableCounter = 0;
		int errorCounter = 0;
		for (Wine wine : catalogManager.getAllWines()) {
			if (catalogManager.isAvailable(wine)) availableCounter++;
			else if (catalogManager.isUnavailable(wine)) unavailableCounter++;
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
	public void deletesAllAvailableWines() {
		Iterable<Wine> availableWines = catalogManager.getAvailableWines();
		for (Wine wine : availableWines) {
			if (inventory.findByProduct(wine).isPresent()) {
				inventory.delete(inventory.findByProduct(wine).get());
			}
			catalogManager.deleteById(wine.getProductId());
		}

		assertThat(catalogManager.getAllWines()).hasSize(5);
		assertThat(catalogManager.getAvailableWines()).hasSize(0);
		assertThat(catalogManager.getUnavailableWines()).hasSize(5);
	}

	// Hinzufügen eines neuen Produktes
	@Test
	public void testsAddingNewProduct() {
		Wine createdWine = catalogManager.createNewProduct(form1);

		ProductIdentifier id = createdWine.getProductId();
		Wine currentWine = catalogManager.findById(id);
		assertThat(currentWine.getItemNr()).isEqualTo(12345);
		assertThat(currentWine.getName()).isEqualTo("TestWein");
		assertThat(currentWine.getBuyPrice()).isEqualTo(Money.of(444, "EUR"));
		assertThat(currentWine.getSellPrice()).isEqualTo(Money.of(555, "EUR"));
		assertThat(currentWine.getWineType()).isEqualTo(Wine.WineType.ROSE);
		assertThat(currentWine.getPic()).isEqualTo("no");
		assertThat(currentWine.getDetails()).isEqualTo("no comment");
	}

	// Editieren eines Artikels
	@Test
	public void testsEditingWineCatalog() {
		for (Wine w : catalogManager.getAllWines()) {
			Boolean result = catalogManager.editProductInCatalog(w, form2);
			assertThat(result).isEqualTo(true);
		}

		for (Wine currentWine : catalogManager.getAllWines()) {
			assertThat(currentWine.getItemNr()).isEqualTo(54321);
			assertThat(currentWine.getName()).isEqualTo("KeinTest");
			assertThat(currentWine.getBuyPrice()).isEqualTo(Money.of(222, "EUR"));
			assertThat(currentWine.getSellPrice()).isEqualTo(Money.of(333, "EUR"));
			assertThat(currentWine.getWineType()).isEqualTo(Wine.WineType.OTHER);
			assertThat(currentWine.getPic()).isEqualTo("yes");
			assertThat(currentWine.getDetails()).isEqualTo("Kein Kommentar");
		}


	}

}

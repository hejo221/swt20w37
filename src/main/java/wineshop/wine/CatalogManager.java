package wineshop.wine;

import com.mysema.commons.lang.Assert;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.salespointframework.core.Currencies.EURO;


@Service
@Transactional
public class CatalogManager {

	private final WineCatalog wineCatalog;

	CatalogManager(WineCatalog wineCatalog) {
		Assert.notNull(wineCatalog, "Weinkatalog darf nicht leer sein.");
		this.wineCatalog = wineCatalog;
	}


	Boolean editProductInCatalog(Wine wine, NewProductForm form) {
		Money buyPrice, sellPrice;
		int itemNr;

		try {
			itemNr = Integer.parseInt(form.getItemNr());
			buyPrice = Money.of(Double.parseDouble(form.getBuyPrice()), EURO);
			sellPrice = Money.of(Double.parseDouble(form.getSellPrice()), EURO);
		} catch (Exception exception) {
			System.out.println("EXCEPTION!");
			return false; //FAILURE HINZUFÜGEN
		}

		wine.setItemNr(itemNr);
		wine.setName(form.getName());
		wine.setBuyPrice(buyPrice);
		wine.setSellPrice(sellPrice);
		wine.setPic(form.getPic());
		wine.setDetails(form.getDetails());

		return true;
	}


	public Streamable<Wine> getAllWines() {
		return wineCatalog.findAll();
	}

	public Streamable<Wine> getAvailableWines() {
		return wineCatalog.findByCategory("available");
	}

	public Wine createNewProduct(NewProductForm form) {
		int itemNr;
		Money buyPrice, sellPrice;

		itemNr = Integer.parseInt(form.getItemNr());
		buyPrice = Money.of(Double.parseDouble(form.getBuyPrice()), EURO);
		sellPrice = Money.of(Double.parseDouble(form.getSellPrice()), EURO);

		return wineCatalog.save(new Wine(itemNr, form.getName(), form.getWineType(), form.getPic(), buyPrice, sellPrice, form.getDetails()));

	}

	public Wine findById(ProductIdentifier id) {
		return wineCatalog.findById(id).get();
	}

	public void deleteById(ProductIdentifier id) {

		wineCatalog.deleteById(id);
	}

	public Boolean isAvailable(Wine wine){
		Streamable<Wine> wines = getAvailableWines();
		for (Wine w : wines){
			if (wine.productId == w.productId) return true;
		}
		return false;
	}
}


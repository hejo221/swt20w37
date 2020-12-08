package wineshop.wine;

import com.mysema.commons.lang.Assert;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.salespointframework.core.Currencies.EURO;


@Service
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
			return false; //FAILURE HINZUFÜGEN
		}

		wine.setItemNr(itemNr);
		wine.setWineName(form.getName());
		wine.setWineType(form.getWineType());
		wine.setBuyPrice(buyPrice);
		wine.setSellPrice(sellPrice);
		wine.setPic(form.getPic());
		wine.setDetails(form.getDetails());
		wineCatalog.save(wine);

		return true;
	}

	public Streamable<Wine> getAllWines() {
		return wineCatalog.findAll();
	}

	public Streamable<Wine> getAvailableWines() {
		return wineCatalog.findByCategory("available");
	}

	public Streamable<Wine> getUnavailableWines() {
		return wineCatalog.findByCategory("unavailable");
	}

	public Wine createNewProduct(NewProductForm form) {
		int itemNr;
		Money buyPrice, sellPrice;
		itemNr = Integer.parseInt(form.getItemNr());
		buyPrice = Money.of(Double.parseDouble(form.getBuyPrice()), EURO);
		sellPrice = Money.of(Double.parseDouble(form.getSellPrice()), EURO);

		Wine savedWine = wineCatalog.save(new Wine(itemNr, form.getName(), form.getWineType(), form.getPic(), buyPrice, sellPrice, form.getDetails()));
		savedWine.addCategory("available");

		return savedWine;
	}

	public Wine findById(ProductIdentifier id) {
		if (wineCatalog.findById(id).isPresent()) return wineCatalog.findById(id).get();
		else return null;
	}

	@Transactional
	public void deleteById(ProductIdentifier id) {

		wineCatalog.deleteById(id);
	}
	@Transactional
	public void makeItemUnavailable(Wine wine) {
		wine.removeCategory("available");
		wine.addCategory("unavailable");

	}

	public Boolean isAvailable(Wine wine) {
		Streamable<Wine> wines = getAvailableWines();
		for (Wine w : wines) {
			if (wine == w) return true;
		}
		return false;
	}

	public Boolean isUnavailable(Wine wine) {
		Streamable<Wine> wines = getAvailableWines();
		for (Wine w : wines) {
			if (wine == w) return false;
		}
		return true;
	}
}


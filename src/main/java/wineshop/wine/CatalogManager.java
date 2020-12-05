package wineshop.wine;

import com.mysema.commons.lang.Assert;
import org.hibernate.metamodel.model.domain.IdentifiableDomainType;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static org.salespointframework.core.Currencies.EURO;

@Service
@Transactional
public class CatalogManager {

	private final WineCatalog wineCatalog;

	CatalogManager(WineCatalog wineCatalog) {
		Assert.notNull(wineCatalog, "Weinkatalog darf nicht leer sein.");
		this.wineCatalog = wineCatalog;
	}


	@Transactional
	Boolean editProductInCatalog(ProductIdentifier productId, NewProductForm form) {
		Money buyPrice, sellPrice;
		Wine wine;

		int itemNr;

		try {
			itemNr = Integer.parseInt(form.getItemNr());
			buyPrice = Money.of(Double.parseDouble(form.getBuyPrice()), EURO);
			sellPrice = Money.of(Double.parseDouble(form.getSellPrice()), EURO);
		} catch (Exception exception) {
			System.out.println("EXCEPTION!");
			return false; //FAILURE HINZUFÜGEN
		}

		Optional<Wine> wines = wineCatalog.findById(productId);
		if (wines.isPresent()) {
			wine = wines.get();
		} else return false;

		System.out.println("In CatalogManager" + wine.getProductId());
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

	public void createNewProduct(NewProductForm form) {
		int itemNr;
		Money buyPrice, sellPrice;

		itemNr = Integer.parseInt(form.getItemNr());
		buyPrice = Money.of(Double.parseDouble(form.getBuyPrice()), EURO);
		sellPrice = Money.of(Double.parseDouble(form.getSellPrice()), EURO);

		wineCatalog.save(new Wine(itemNr, form.getName(), form.getWineType(), form.getPic(), buyPrice, sellPrice, form.getDetails()));

	}

	public Wine findById(ProductIdentifier id) {
		return wineCatalog.findById(id).get();
	}

	public void deleteById(ProductIdentifier id) {

		wineCatalog.deleteById(id);
	}



}


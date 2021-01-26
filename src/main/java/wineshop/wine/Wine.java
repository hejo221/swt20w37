package wineshop.wine;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.quantity.Quantity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import javax.persistence.Entity;
import java.util.Locale;

@Entity
public class Wine extends Product {

	public ProductIdentifier productId;
	public enum WineType {RED, WHITE, ROSE, SPARKLING, FRUITWINE, OTHER}
	private int itemNr;
	private String pic, details;
	private Money buyPrice;
	private WineType wineType;
	private Quantity minAmount;
	private Integer maxAmount;

	@SuppressWarnings({"unused", "deprecation"})
	public Wine() {
	}

	public Wine(Integer itemNr, String name, WineType wineType, String pic, Money buyPrice, Money sellPrice, String details) {

		super(name, sellPrice);
		Assert.notNull(itemNr, "Item Number must not be null!");
		Assert.notNull(pic, "Picture must not be null!");
		Assert.notNull(wineType, "Wine Type must not be null!");
		Assert.notNull(buyPrice, "Buy Price must not be null!");
		Assert.notNull(details, "Details must not be null!");

		this.itemNr = itemNr;
		this.pic = pic;
		this.wineType = wineType;
		this.buyPrice = buyPrice;
		this.details = details;
		this.minAmount = Quantity.of(10);
		this.maxAmount = 50;
		productId = super.getId();
	}

	public ProductIdentifier getProductId(){return productId;}
	//GETTERS

	public int getItemNr() {
		return itemNr;
	}

	public Quantity getMinAmount() { return minAmount; }

	public Integer getMaxAmount() { return maxAmount; }

	//super.getName()

	public String getPic() {
		return pic;
	}

	public Money getBuyPrice() {
		return buyPrice;
	}


	// Gibt den Preis im Format XX.XXX,YY € zurück

	public String getBuyPrice2() {

		MonetaryAmountFormat formatDEU = MonetaryFormats.getAmountFormat(Locale.GERMANY);

		return formatDEU.format(getPrice()).replace(" EUR", ""). replace(",00", ",-");
	}

	public Number getBuyPriceNumber(){
		return buyPrice.getNumber();
	}
	public Money getSellPrice() {
		return (Money) super.getPrice();
	}

	public Number getSellPriceNumber(){
		return getSellPrice().getNumber();
	}

	public String getSellPrice2() {

		MonetaryAmountFormat formatDEU = MonetaryFormats.getAmountFormat(Locale.GERMANY);

		return formatDEU.format(getSellPrice()).replace(" EUR", ""). replace(",00", ",-");
	}

	public String getDetails() {
		return details;
	}

	public WineType getWineType(){
		return this.wineType;
	}


	public String getWineTypeAufDeutsch() {
		if (this.wineType==WineType.RED ) return "Rotwein";
		else if (this.wineType==WineType.WHITE ) return "Weißwein";
		else if (this.wineType==WineType.FRUITWINE ) return "Obstwein";
		else if (this.wineType==WineType.ROSE ) return "Roséwein";
		else if (this.wineType==WineType.SPARKLING ) return "Schaumwein";
		else return "Anderes";
	}

	//SETTERS

	public void setItemNr(int itemNr) {
		this.itemNr = itemNr;
	}

	public void setWineName (String name){
		super.setName(name);
	}

	public void setWineType(WineType wineType) {
		this.wineType = wineType;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public void setBuyPrice(Money buyPrice) {
		this.buyPrice = buyPrice;
	}

	public void setSellPrice(Money sellPrice){
		super.setPrice(sellPrice);
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public  void setMinAmount(Quantity quantity) { this.minAmount = quantity; }

	public void setMaxAmount(Integer integer) { this.maxAmount = integer; }
}
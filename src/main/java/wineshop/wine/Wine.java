package wineshop.wine;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import javax.persistence.Entity;

@Entity
public class Wine extends Product {


	public enum WineType {RED, WHITE, ROSE, SPARKLING, FRUITWINE, OTHER}
	private int itemNr;
	private String pic, details;
	private Money buyPrice;
	private WineType wineType;

	@SuppressWarnings({"unused", "deprecation"})
	public Wine() {
	}

	public Wine(Integer itemNr, String name, WineType wineType, String pic, Money buyPrice, Money sellPrice, String details) {
		super(name, sellPrice);
		this.itemNr = itemNr;
		this.pic = pic;
		this.wineType = wineType;
		this.buyPrice = buyPrice;
		this.details = details;
	}

	//GETTERS

	public int getItemNr() {
		return itemNr;
	}

	//super.getName()

	public String getPic() {
		return pic;
	}

	public Money getBuyPrice() {
		return buyPrice;
	}

	//super.getPrice()

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

	//super.setName()

	public void setPic(String pic) {
		this.pic = pic;
	}

	public void setBuyPrice(Money buyPrice) {
		this.buyPrice = buyPrice;
	}

	//super.getPrice()

	public void setDetails(String details) {
		this.details = details;
	}
}

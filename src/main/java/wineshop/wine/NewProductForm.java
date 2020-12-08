package wineshop.wine;
import javax.validation.constraints.NotEmpty;

class NewProductForm {
	//@NotEmpty(message = "{NewProductForm.itemNr.NotEmpty}") //
	private final String itemNr;

	@NotEmpty(message = "{NewProductForm.name.NotEmpty}")
	private final String name;

	//@NotEmpty(message = "{NewProductForm.wineType.NotEmpty}")
	private final Wine.WineType wineType;

	@NotEmpty(message = "{NewProductForm.pic.NotEmpty}")
	private final String pic;

	@NotEmpty(message = "{NewProductForm.buyPrice.NotEmpty}")
	private final String buyPrice;

	@NotEmpty(message = "{NewProductForm.sellPrice.NotEmpty}")
	private final String sellPrice;

	@NotEmpty(message = "{NewProductForm.name.NotEmpty}")
	private final String details;


	public NewProductForm(String itemNr, String name, Wine.WineType wineType, String pic, String buyPrice, String sellPrice, String details) {
		this.itemNr = itemNr;
		this.name = name;
		this.wineType = wineType;
		this.pic = pic;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.details = details;
	}

	public String getItemNr() {
		return itemNr;
	}

	public String getName() {
		return name;
	}

	public Wine.WineType getWineType() {
		return wineType;
	}

	public String getPic() {
		return pic;
	}

	public String getDetails(){
		return details;
	}

	public String getBuyPrice(){return buyPrice;}

	public String getSellPrice(){return sellPrice;}

}



package wineshop.wine;

import org.javamoney.moneta.Money;
import wineshop.wine.Wine;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

import static org.salespointframework.core.Currencies.EURO;

public class WineRegistrationForm {

	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}") //
	private String name;

	@NotNull(message = "{RegistrationForm.price.NotNull}") //
	private Number price;

	@NotNull(message = "{RegistrationForm.price.NotNull}") //
	private String image;

	@NotEmpty(message = "{RegistrationForm.description.NotEmpty}") //
	private String description;

	@NotEmpty(message = "{RegistrationForm.productType.NotEmpty}") //
	private String type;


	public WineRegistrationForm(String name, Number price, String image, String description, String type) {
		this.name = name;
		this.price = price;
		this.image = image;
		this.description = description;
		this.type = type;
	}


	public String getName() {
		return name;
	}

	public Number getPrice() { return price; }

	public String getImage() { return image; }

	public String getDescription() {
		return description;
	}

	public String getType() { return type; }
}

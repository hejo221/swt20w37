package wineshop.order;


public class CartCustForm {
	private Number customerId;


	public CartCustForm(Number customerId) {
		this.customerId = customerId;
	}


	public Number  getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long id) {
		this.customerId = id;
	}
}

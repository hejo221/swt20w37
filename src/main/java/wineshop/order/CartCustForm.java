package wineshop.order;


public class CartCustForm {
	private Number customerId;
	private final String paymentMethod;


	public CartCustForm(Number customerId, String paymentMethod) {
		this.customerId = customerId;
		this.paymentMethod = paymentMethod;
	}


	public Number getCustomerId() {
		return customerId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setCustomerId(long id) {
		this.customerId = id;
	}

}

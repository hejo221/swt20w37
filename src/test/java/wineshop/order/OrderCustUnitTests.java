package wineshop.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@ExtendWith(MockitoExtension.class)
public class OrderCustUnitTests {

	@Mock
	UserAccount userAccount;

	@Test
	public void rejectsNullOrderType() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> new OrderCust(userAccount, Cash.CASH, null));
	}


	@Test
	public void rejectsNullCustomerOrOrderType() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> new OrderCust(userAccount, Cash.CASH, null, null));
	}


	@Test
	public void getPaymentMethodString() {
		OrderCust order = new OrderCust(userAccount, Cash.CASH, OrderType.ORDER);
		assertThat(order.getPaymentMethodString()).isEqualTo("Bargeld");
	}


	@Test
	public void isOrder() {
		OrderCust order = new OrderCust(userAccount, Cash.CASH, OrderType.ORDER);
		assertThat(order.isOrder()).isEqualTo(true);
	}


	@Test
	public void isPreorder() {
		OrderCust preorder = new OrderCust(userAccount, Cash.CASH, OrderType.PREORDER);
		assertThat(preorder.isPreorder()).isEqualTo(true);
	}


	@Test
	public void isReorder() {
		OrderCust reorder = new OrderCust(userAccount, Cash.CASH, OrderType.REORDER);
		assertThat(reorder.isReorder()).isEqualTo(true);
	}


}




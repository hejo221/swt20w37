package wineshop.order;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import wineshop.AbstractIntegrationTests;
import wineshop.customer.Customer;
import wineshop.customer.CustomerRepository;
import wineshop.inventory.InventoryManager;
import wineshop.wine.CatalogManager;
import wineshop.wine.Wine;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class OrderCustManagerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	UserAccountManagement userAccountManagement;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	OrderCustManager orderCustManager;

	@Autowired
	UniqueInventory<UniqueInventoryItem> inventory;

	@Autowired
	InventoryManager inventoryManager;

	@Autowired
	CatalogManager catalogManager;


	@Test
	public void cartToOrderAndPreOrder() {

		Iterable<UserAccount> allUserAccounts = userAccountManagement.findAll();
		UserAccount userAccount = allUserAccounts.iterator().next();

		Iterable<Wine> allWines = catalogManager.getAllWines();
		Wine wine = allWines.iterator().next();

		Iterable<Customer> allCustomers = customerRepository.findAll();
		Customer customer = allCustomers.iterator().next();
		long customerID = customer.getId();

		Cart cart = new Cart();
		cart.addOrUpdateItem(wine, Quantity.of(10));

		CartCustForm cartCustForm = new CartCustForm ( customerID,"Bargeld");

		customerRepository.save(customer);
		orderCustManager.cartToOrderAndPreOrder(userAccount, cart, cartCustForm);
		assertThat(cart.isEmpty()).isEqualTo(true);
	}


}



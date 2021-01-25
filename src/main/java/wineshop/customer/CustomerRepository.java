package wineshop.customer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import javax.transaction.Transactional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

	@Override
	Streamable<Customer> findAll();

	@Transactional
	void deleteCustomerById(Long id);

}


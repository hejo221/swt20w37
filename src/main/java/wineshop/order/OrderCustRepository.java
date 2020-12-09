package wineshop.order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import javax.transaction.Transactional;

public interface OrderCustRepository extends CrudRepository<OrderCust, Long>{

	@Override
	Streamable<OrderCust> findAll();

//	@Transactional
//	void deleteOrderCustById(Long id);
}
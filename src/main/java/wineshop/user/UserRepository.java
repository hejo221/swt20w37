package wineshop.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import javax.transaction.Transactional;

public interface UserRepository extends CrudRepository<User, Long> {

	@Override
	Streamable<User> findAll();

	@Transactional
	void deleteById(Long id);
}

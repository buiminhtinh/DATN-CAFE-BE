package cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cafe.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String>{
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



import cafe.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
	Optional<Account> findByUsername( String username);
	
	@Query("SELECT DISTINCT ar.account FROM Authority ar WHERE ar.role.id IN (1,2)")
	List<Account> getAdministrators();
	//1 là admin, 2 là staff, 3 là customer
	List<Account> findByUsernameContainsIgnoreCase(String username);

}

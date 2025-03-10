package cafe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cafe.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
	Optional<Account> findByUsername(String username);
    long count(); // Phương thức đếm tổng số tài khoản
	
    @Query("""
    	    SELECT DISTINCT ar.account 
    	    FROM Authority ar 
    	    WHERE ar.role.id IN (1, 2) 
    	    AND ar.account.id NOT IN (
    	        SELECT ar2.account.id 
    	        FROM Authority ar2 
    	        WHERE ar2.role.id = 4
    	    )
    	""")

    List<Account> getAdministrators();

    @Query("SELECT DISTINCT ar.account FROM Authority ar WHERE ar.role.id IN (1, 2)   AND ar.account.id NOT IN (\r\n"
    		+ "    	        SELECT ar2.account.id \r\n"
    		+ "    	        FROM Authority ar2 \r\n"
    		+ "    	        WHERE ar2.role.id = 4\r\n"
    		+ "    	    ) AND LOWER(ar.account.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<Account> getAdministratorsByUsernameContains(@Param("username") String username);

	//1 là admin, 2 là staff, 3 là customer
	List<Account> findByUsernameContainsIgnoreCase(String username);

	List<Account> findByPhoneContainsIgnoreCase(String phone);
	
	Optional<Account> findByPhone(String phone);
	Optional<Account> findByEmail(String email);

	
	boolean existsByUsername(String username);
	 @Query("SELECT COUNT(a) FROM Account a WHERE a.active = true")
	    int countActiveAccounts();

}

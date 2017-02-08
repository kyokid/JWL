package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Entaard on 2/7/17.
 */
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    List<AccountEntity> findAll();
}

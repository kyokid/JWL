package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Entaard on 2/7/17.
 */
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    List<AccountEntity> findAll();

    @Transactional
    @Modifying
    @Query("update AccountEntity a set a.inLibrary = ?1 where a.userId = ?2")
    int setStateOfAccount(boolean state, String userId);
}

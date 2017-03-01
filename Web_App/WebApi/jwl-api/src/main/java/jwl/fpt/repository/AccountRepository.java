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
    int setStatus(boolean state, String userId);

    // TODO: only list a user's borrowing copies (get rid of default @Where in AccountEntity)
//    @Query("select distinct acc from AccountEntity acc " +
//            "inner join acc.borrowedBookCopies c1 " +
//            "where c1.returnDate is null " +
//            "and acc.userId = ?1")
    AccountEntity findByUserId(String userId);

    @Transactional
    @Modifying
    @Query("update AccountEntity a set a.googleToken = ?1 where a.userId = ?2")
    int updateGoogleToken(String googleToken, String userId);

    @Query("select acc.inLibrary from AccountEntity acc where acc.userId = ?1")
    Boolean getStatus(String userId);

    // TODO: check for delete_date.
    @Query("select acc.userId " +
            "from AccountEntity acc " +
            "where acc.userId = ?1 and acc.activated = true and acc.inLibrary = true")
    String checkBorrower(String userId);

    // TODO: check for delete_date.
    @Query("select acc.userId " +
            "from AccountEntity acc " +
            "where acc.userId = ?1 and acc.activated = true")
    String checkBorrowerByLibrarian(String userId);
}

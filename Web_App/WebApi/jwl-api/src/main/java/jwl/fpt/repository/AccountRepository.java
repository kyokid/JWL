package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Entaard on 2/7/17.
 */
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    @Query("select user " +
            "from AccountEntity user " +
            "where user.userId = ?1 and user.password = ?2 and user.activated = true")
    AccountEntity login(String userId, String password);

    @Query("select p from ProfileEntity p where p.userId = ?1")
    ProfileEntity findProfileByUserId(String userId);

    @Query("select users from AccountEntity users where users.deleteDate is null")
    List<AccountEntity> findAllUsers();

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

    @Query("select users from AccountEntity users where lower(users.userId) like lower(?1) and users.deleteDate is null")
    List<AccountEntity> findByUserIdLike(String term);

    @Transactional
    @Modifying
    @Query("update AccountEntity a set a.googleToken = ?1 where a.userId = ?2")
    int updateGoogleToken(String googleToken, String userId);

    @Query("select acc.inLibrary from AccountEntity acc where acc.userId = ?1")
    Boolean getStatus(String userId);

    @Query("select acc.userId " +
            "from AccountEntity acc " +
            "where acc.userId = ?1 " +
            "and acc.deleteDate is null " +
            "and acc.activated = true " +
            "and acc.inLibrary = true")
    String checkBorrower(String userId);

    @Query("select acc.userId " +
            "from AccountEntity acc " +
            "where acc.userId = ?1 " +
            "and acc.deleteDate is null " +
            "and acc.activated = true")
    String checkBorrowerByLibrarian(String userId);

//    @Transactional
//    @Modifying
//    @Query("update AccountEntity a set a.checkinKey = ?1 where a.userId = ?2")
//    int updateCheckinKey(String checkinKey, String userId);

//    @Query("select acc.checkinKey from AccountEntity acc where acc.userId = ?1")
//    String getCheckinKey(String userId);
}

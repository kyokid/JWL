package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BorrowedBookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
public interface BorrowedBookCopyRepo extends JpaRepository<BorrowedBookCopyEntity, Integer> {
//    @Query("select b from BorrowedBookCopyEntity b where b.userId = ?1")
    List<BorrowedBookCopyEntity> findByAccount(AccountEntity entity);

    @Transactional
    @Modifying
    @Query("delete from BorrowedBookCopyEntity b where b.account.userId = ?1 and b.id = ?2")
    void deleteByUserIdAndBorrowedCopyId(String userId, Integer borrowedCopyId);
}

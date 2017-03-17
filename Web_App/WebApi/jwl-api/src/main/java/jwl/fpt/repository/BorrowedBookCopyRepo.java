package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BorrowedBookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
public interface BorrowedBookCopyRepo extends JpaRepository<BorrowedBookCopyEntity, Integer> {
//    @Query("select b from BorrowedBookCopyEntity b where b.userId = ?1")
    List<BorrowedBookCopyEntity> findByAccountAndReturnDateIsNull(AccountEntity entity);

    @Transactional
    @Modifying
    @Query("delete from BorrowedBookCopyEntity b " +
            "where b.account.userId = ?1 " +
            "and b.bookCopy.rfid = ?2 " +
            "and b.returnDate is null")
    void deleteByUserIdAndBorrowedCopyRfid(String userId, String borrowedCopyId);

    BorrowedBookCopyEntity findByBookCopy_RfidAndReturnDateIsNull(String rfid);

    @org.springframework.transaction.annotation.Transactional
    @Modifying
    @Query("update BorrowedBookCopyEntity b set b.returnDate = ?1 where b.bookCopy.rfid = ?2")
    int updateReturnDate(Date currentDate, String rfid);
}

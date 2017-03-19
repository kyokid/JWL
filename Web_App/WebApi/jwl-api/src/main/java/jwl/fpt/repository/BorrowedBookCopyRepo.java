package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BookCopyEntity;
import jwl.fpt.entity.BorrowedBookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

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

    BorrowedBookCopyEntity findFirst1ByBookCopyAndReturnDateIsNull(BookCopyEntity bookCopyEntity);

    @Query("select copies " +
            "from BorrowedBookCopyEntity copies " +
            "where copies.bookCopy.rfid in ?1 and copies.returnDate is null")
    List<BorrowedBookCopyEntity> findByRfids(Set<String> rfids);

    BorrowedBookCopyEntity findByBookCopy_RfidAndReturnDateIsNull(String rfid);

    @Transactional
    @Modifying
    @Query("update BorrowedBookCopyEntity b set b.returnDate = ?1 where b.bookCopy.rfid = ?2")
    int updateReturnDate(Date currentDate, String rfid);

    @Query("select copies " +
            "from BorrowedBookCopyEntity copies " +
            "where copies.bookCopy.book.id = ?1 and copies.returnDate is null")
    List<BorrowedBookCopyEntity> findBorrowingCopiesOfBook(Integer bookId);

    List<BorrowedBookCopyEntity> findByReturnDateIsNull();
}

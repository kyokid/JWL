package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BookCopyEntity;
import jwl.fpt.entity.BorrowedBookCopyEntity;
import jwl.fpt.entity.BorrowerTicketEntity;
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

    @Transactional
    @Modifying
    @Query("update BorrowedBookCopyEntity b set b.returnDate = ?1, b.rootId = b.id where b.bookCopy.rfid = ?2")
    int updateReturnDateWhereExtendEquals0(Date currentDate, String rfid);

    @Query("select copies " +
            "from BorrowedBookCopyEntity copies " +
            "where copies.bookCopy.book.id = ?1 and copies.returnDate is null")
    List<BorrowedBookCopyEntity> findBorrowingCopiesOfBook(Integer bookId);

    List<BorrowedBookCopyEntity> findByReturnDateIsNull();

    @Query(value = "select copies " +
            "from BorrowedBookCopyEntity copies " +
            "where copies.account.userId = ?1 and copies.rootId is NULL and copies.returnDate IS NOT NULL")
    List<BorrowedBookCopyEntity> findByUserIdAndRootIdNULL(String userId);

    @Query(value = "SELECT * " +
            "FROM borrowed_book_copy a " +
            "INNER JOIN " +
            "(SELECT root_id, max(extend_number) as last_extend_number " +
            "FROM borrowed_book_copy " +
            "WHERE root_id IS NOT NULL " +
            "GROUP BY root_id) b " +
            "ON a.root_id = b.root_id AND a.extend_number = b.last_extend_number " +
            "WHERE a.return_date IS NOT NULL AND a.user_id = ?1" , nativeQuery = true)
    List<BorrowedBookCopyEntity> getListLast(String userId);

    @Query(value = "SELECT * " +
            "FROM borrowed_book_copy a " +
            "INNER JOIN " +
            "(SELECT root_id, min(extend_number) as first_extend_number " +
            "FROM borrowed_book_copy " +
            "WHERE root_id IS NOT NULL " +
            "GROUP BY root_id) b " +
            "ON a.root_id = b.root_id AND a.extend_number = b.first_extend_number " +
            "WHERE a.return_date IS NOT NULL AND a.user_id = ?1" , nativeQuery = true)
    List<BorrowedBookCopyEntity> getListFirst(String userId);
}

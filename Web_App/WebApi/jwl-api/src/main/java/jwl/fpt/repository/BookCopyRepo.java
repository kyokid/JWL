package jwl.fpt.repository;

import jwl.fpt.entity.BookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by Entaard on 1/29/17.
 */
public interface BookCopyRepo extends JpaRepository<BookCopyEntity, String> {
    @Query("select distinct books from BookCopyEntity books " +
            "left join books.borrowedBookCopies b1 " +
            "where books.rfid not in " +
            "(select distinct b2.bookCopy.rfid from BorrowedBookCopyEntity b2 where b2.returnDate is null) " +
            "and books.rfid in ?1")
    List<BookCopyEntity> findAvailableCopies(Set<String> rfids);

    @Query("select distinct book from BookCopyEntity book " +
            "left join book.borrowedBookCopies b1 " +
            "where book.rfid = ?1 " +
            "and book.rfid not in " +
            "(select distinct b2.bookCopy.rfid " +
            "from BorrowedBookCopyEntity b2 " +
            "where b2.returnDate is null)")
    BookCopyEntity findAvailableCopy(String rfid);
}

package jwl.fpt.repository;

import jwl.fpt.entity.WishBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by thiendn on 22/03/2017.
 */
public interface WishBookRepository extends JpaRepository<WishBookEntity, String> {
    @Transactional
    @Modifying
    @Query(value = "insert into wish_book (user_id, book_id) values (?1, ?2)", nativeQuery = true)
    int insert(String userId, int bookId);

    @Transactional
    @Modifying
    @Query(value = "delete from WishBookEntity wb where wb.account.userId = ?1 and wb.book.id = ?2")
    int delete(String userId, int bookId);
}

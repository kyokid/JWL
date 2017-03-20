package jwl.fpt.repository;

import jwl.fpt.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Entaard on 1/31/17.
 */
public interface BookRepo extends JpaRepository<BookEntity, Integer> {
    BookEntity findById(int bookId);

    @Query(value = "SELECT b from BookEntity b where trgm_match(b.title, ?1) > 0.1 order by trgm_match(b.title, ?1)")
//    @Query("SELECT b from BookEntity b where b.title = ?1")
    List<BookEntity> searchBooks(String term);
}

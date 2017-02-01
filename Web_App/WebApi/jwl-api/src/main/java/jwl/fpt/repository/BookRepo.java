package jwl.fpt.repository;

import jwl.fpt.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Entaard on 1/31/17.
 */
public interface BookRepo extends JpaRepository<BookEntity, Integer> {
}

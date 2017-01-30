package jwl.fpt.repository;

import jwl.fpt.entity.BookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Entaard on 1/29/17.
 */
public interface BookCopyRepo extends JpaRepository<BookCopyEntity, String> {
}

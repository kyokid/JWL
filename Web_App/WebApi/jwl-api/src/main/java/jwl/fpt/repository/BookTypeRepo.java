package jwl.fpt.repository;

import jwl.fpt.entity.BookTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Entaard on 1/31/17.
 */
public interface BookTypeRepo extends JpaRepository<BookTypeEntity, Integer> {
    BookTypeEntity findById(int bookTypeId);
}

package jwl.fpt.repository;

import jwl.fpt.entity.BorrowedBookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Entaard on 1/29/17.
 */
public interface BorrowedBookCopyRepo extends JpaRepository<BorrowedBookCopyEntity, Integer> {
}

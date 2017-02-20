package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BorrowedBookCopyEntity;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
public interface BorrowedBookCopyRepo extends JpaRepository<BorrowedBookCopyEntity, Integer> {
//    @Query("select b from BorrowedBookCopyEntity b where b.userId = ?1")
    List<BorrowedBookCopyEntity> findByAccount(AccountEntity entity);
}

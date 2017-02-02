package jwl.fpt.repository;

import jwl.fpt.entity.BorrowerTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Entaard on 1/30/17.
 */
public interface BorrowerTicketRepo extends JpaRepository<BorrowerTicketEntity, String> {
    BorrowerTicketEntity findByUserIdAndDeleteDateIsNull(String userId);
}

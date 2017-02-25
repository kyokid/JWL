package jwl.fpt.repository;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BorrowerTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;

/**
 * Created by Entaard on 1/30/17.
 */
public interface BorrowerTicketRepo extends JpaRepository<BorrowerTicketEntity, String> {
    BorrowerTicketEntity findByAccountAndDeleteDateIsNull(AccountEntity accountEntity);

    //by thiendn 20/2/2017
    @Query(value = "insert into borrower_ticket (qr_id, user_id, create_date, scan_date) values (?1, ?2, ?3, ?4)",
            nativeQuery = true)
    void createNewTicket(String ticketId, String userId, Date create_date, Date scan_date);
}

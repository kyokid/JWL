package jwl.fpt.service.imp;

import jwl.fpt.entity.BorrowedBookCopyEntity;
import jwl.fpt.entity.BorrowerTicketEntity;
import jwl.fpt.model.dto.BookCopyDto;
import jwl.fpt.model.dto.BookCopyDtoList;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.repository.BorrowerTicketRepo;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.ExpiringSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
@Service
public class BookBorrowService implements IBookBorrowService {
    @Autowired
    private SessionRepository<ExpiringSession> sessionRepo;
    @Autowired
    private BorrowerTicketRepo borrowerTicketRepo;

    @Override
    @Transactional
    public boolean initBorrowSession(HttpServletRequest request, BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        String iBeaconId = borrowerDto.getIBeaconId();
        String userId = borrowerDto.getUserId();
        HttpSession session = request.getSession(true);
        session.setAttribute(Constant.IBEACON_ID, iBeaconId);
        session.setAttribute(Constant.SESSION_BORROWER, userId);
        session.setMaxInactiveInterval(30);

        BorrowerTicketEntity borrowerTicketEntity = borrowerTicketRepo.findByUserIdAndDeleteDateIsNull(userId);
        borrowerTicketEntity.setIbeaconId(iBeaconId);
        borrowerTicketEntity.setSessionId(session.getId());
        return true;
    }

    @Override
    public BookCopyDtoList addCopiesToSession(BookCopyDtoList bookCopyDtoList) {
        // TODO: Add necessary validations.
        String ibeaconId = bookCopyDtoList.getIbeaconId();
        BorrowerTicketEntity borrowerTicketEntity = borrowerTicketRepo.findByIbeaconIdAndDeleteDateIsNull(ibeaconId);

        if (borrowerTicketEntity == null) {
            return null;
        }

        String sessionId = borrowerTicketEntity.getSessionId();
        Session session = sessionRepo.getSession(sessionId);
//
        if (session == null) {
            return null;
        }
//
        session.setAttribute(Constant.SESSION_PENDING_COPIES, bookCopyDtoList);

        BookCopyDtoList list = session.getAttribute(Constant.SESSION_PENDING_COPIES);
        System.out.print(list.toString());

        return bookCopyDtoList;
    }

    @Override
    public List<BorrowedBookCopyDto> saveBorrowedCopies(HttpServletRequest request) {
        // TODO: Add necessary validations.

        HttpSession session = request.getSession(false);
        List<BookCopyDto> bookCopyDtos = (List<BookCopyDto>) session.getAttribute(Constant.SESSION_PENDING_COPIES);
        String userId = (String) session.getAttribute(Constant.SESSION_BORROWER);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = new ArrayList<>();

        for (BookCopyDto bookCopyDto:
             bookCopyDtos) {
            BorrowedBookCopyEntity entity = new BorrowedBookCopyEntity();
            entity.setUserId(userId);
            String rfid = bookCopyDto.getRfid();
            entity.setBookCopyId(rfid);
            entity.setBorrowedDate(new Date(Calendar.getInstance().getTimeInMillis()));
            // TODO: WIP
        }
        return null;
    }
}

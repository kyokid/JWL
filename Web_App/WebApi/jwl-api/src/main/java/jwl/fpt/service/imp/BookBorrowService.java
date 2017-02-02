package jwl.fpt.service.imp;

import jwl.fpt.entity.*;
import jwl.fpt.model.dto.BookCopyDto;
import jwl.fpt.model.dto.RfidDtoList;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.repository.*;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.util.Constant;
import jwl.fpt.util.Helper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private BorrowerTicketRepo borrowerTicketRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private BookTypeRepo bookTypeRepo;
    @Autowired
    private BorrowedBookCopyRepo borrowedBookCopyRepo;
    @Autowired
    private BookCopyRepo bookCopyRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public boolean initBorrowSession(HttpServletRequest request, BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        String iBeaconId = borrowerDto.getIBeaconId();
        String userId = borrowerDto.getUserId();
        HttpSession session = request.getSession(true);
        session.setAttribute(Constant.SESSION_BORROWER, userId);
        session.setMaxInactiveInterval(10);

        BorrowerTicketEntity borrowerTicketEntity = borrowerTicketRepo.findByUserIdAndDeleteDateIsNull(userId);
        borrowerTicketEntity.setIbeaconId(iBeaconId);
        borrowerTicketEntity.setSessionId(session.getId());
        return true;
    }

    /*
    It seems that Spring's SessionRepository cannot setAttribute to the session,
    or the attribute will not be saved in the database.
    -> Spring team recommends: only interact with normal HttpSession.
    QUICKFIX: in this function, we find the user_id saved in the initBorrowSession step,
    and create and save it in a new session.

    In short, we init 1 session to hold the borrower id, then create another session to
    hold the real transaction of the borrower and the copies.
     */
    @Override
    @Transactional
    public RfidDtoList addCopiesToSession(HttpServletRequest request, RfidDtoList rfidDtoList) {
        // TODO: Add necessary validations.
        SessionRepository<Session> sessionRepo = (SessionRepository<Session>)
                request.getAttribute(SessionRepository.class.getName());
        String ibeaconId = rfidDtoList.getIbeaconId();
        BorrowerTicketEntity borrowerTicketEntity = borrowerTicketRepo.findByIbeaconIdAndDeleteDateIsNull(ibeaconId);
        if (borrowerTicketEntity == null) {
            return null;
        }

        String initSessionId = borrowerTicketEntity.getSessionId();
        Session initSession = sessionRepo.getSession(initSessionId);
        if (initSession == null) {
            return null;
        }

        HttpSession transactionalSession = request.getSession(true);
        transactionalSession.setAttribute(Constant.SESSION_BORROWER, initSession.getAttribute(Constant.SESSION_BORROWER));
        transactionalSession.setAttribute(Constant.SESSION_PENDING_COPIES, rfidDtoList);
        transactionalSession.setMaxInactiveInterval(20);
        borrowerTicketEntity.setSessionId(transactionalSession.getId());
        sessionRepo.delete(initSession.getId());

        return rfidDtoList;
    }

    @Override
    @Transactional
    public List<BorrowedBookCopyDto> checkoutSession(HttpServletRequest request, String userId) {
        // TODO: Add necessary validations.
        BorrowerTicketEntity borrowerTicketEntity = borrowerTicketRepo.findByUserIdAndDeleteDateIsNull(userId);
        String sessionId = borrowerTicketEntity.getSessionId();
        System.out.println(sessionId);
        SessionRepository<Session> sessionRepo = (SessionRepository<Session>)
                request.getAttribute(SessionRepository.class.getName());
        Session session = sessionRepo.getSession(sessionId);
        List<BorrowedBookCopyDto> result = saveBorrowedCopies(session);
        borrowerTicketEntity.setDeleteDate(new Date(Calendar.getInstance().getTimeInMillis()));
        sessionRepo.delete(session.getId());

        return result;
    }

    private List<BorrowedBookCopyDto> saveBorrowedCopies(Session session) {
        // TODO: Add necessary validations.
        RfidDtoList rfidDtoList = session.getAttribute(Constant.SESSION_PENDING_COPIES);
        if (rfidDtoList == null) {
            return null;
        }
        // TODO: Create sample data.
        List<String> rfids = rfidDtoList.getRfids();
        List<BookCopyEntity> bookCopyEntities = bookCopyRepo.findAll(rfids);

        String userId = session.getAttribute(Constant.SESSION_BORROWER);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = new ArrayList<>();

        for (BookCopyEntity bookCopyEntity:
             bookCopyEntities) {
            BookEntity bookEntity = bookRepo.findById(bookCopyEntity.getBookId());
            BookTypeEntity bookTypeEntity = bookTypeRepo.findById(bookEntity.getBookTypeId());
            BorrowedBookCopyEntity entity = new BorrowedBookCopyEntity();

            entity.setUserId(userId);
            entity.setBookCopyId(bookCopyEntity.getRfid());
            entity.setBorrowedDate(new Date(Calendar.getInstance().getTimeInMillis()));
            Date deadline = Helper.GetDateAfter(entity.getBorrowedDate(), bookTypeEntity.getBorrowLimitDays());
            entity.setDeadlineDate(deadline);
            entity.setExtendNumber(0);

            borrowedBookCopyEntities.add(entity);
        }
        borrowedBookCopyEntities = borrowedBookCopyRepo.save(borrowedBookCopyEntities);
        if (borrowedBookCopyEntities.isEmpty()) {
            return null;
        }

        List<BorrowedBookCopyDto> result = new ArrayList<>();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                borrowedBookCopyEntities) {
            BorrowedBookCopyDto dto = modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
            result.add(dto);
        }
        return result;
    }
}

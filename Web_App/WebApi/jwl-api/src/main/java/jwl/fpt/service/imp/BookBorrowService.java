package jwl.fpt.service.imp;

import jwl.fpt.entity.BorrowedBookCopyEntity;
import jwl.fpt.model.dto.BookCopyDto;
import jwl.fpt.model.dto.BookCopyDtoList;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.ExpiringSession;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Entaard on 1/29/17.
 */
@Service
public class BookBorrowService implements IBookBorrowService {
    @Autowired
    private SessionRepository<ExpiringSession> sessionRepo;
    private String id;

    @Override
    public boolean initBorrowSession(HttpServletRequest request, BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        HttpSession session = request.getSession(true);
        session.setAttribute(Constant.IBEACON_ID, borrowerDto.getIBeaconId());
        session.setAttribute(Constant.SESSION_BORROWER, borrowerDto.getUserId());
        id = session.getId();
        session.setMaxInactiveInterval(5*60);
        return true;
    }

    @Override
    public BookCopyDtoList addCopiesToSession(BookCopyDtoList bookCopyDtoList) {
        // TODO: Add necessary validations.
        String ibeaconId = bookCopyDtoList.getIbeaconId();
        Session session = sessionRepo.getSession(id);

        if (session == null) {
            return null;
        }

        // Get only the 1st entry of the map
//        Map.Entry entry = idToSessionMap.entrySet().iterator().next();
//        Session session = (Session) entry.getValue();
        session.setAttribute(Constant.SESSION_PENDING_COPIES, bookCopyDtoList);

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

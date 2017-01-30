package jwl.fpt.service.imp;

import jwl.fpt.entity.BorrowedBookCopyEntity;
import jwl.fpt.model.dto.BookCopyDto;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.util.Constant;
import org.springframework.stereotype.Service;

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
    @Override
    public boolean initBorrowSession(HttpServletRequest request, String userId) {
        // TODO: Add necessary validations.
        HttpSession session = request.getSession(true);
        session.setAttribute(Constant.SESSION_BORROWER, userId);
        session.setMaxInactiveInterval(60*5);
        return true;
    }

    @Override
    public boolean addCopiesToSession(HttpServletRequest request, List<BookCopyDto> bookCopyDtos) {
        // TODO: Add necessary validations.
        HttpSession session = request.getSession(false);

        session.setAttribute(Constant.SESSION_PENDING_COPIES, bookCopyDtos);
        return true;
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

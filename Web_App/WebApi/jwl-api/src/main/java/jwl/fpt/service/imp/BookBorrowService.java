package jwl.fpt.service.imp;

import jwl.fpt.entity.*;
import jwl.fpt.model.BorrowCart;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.model.dto.RfidDtoList;
import jwl.fpt.repository.BookCopyRepo;
import jwl.fpt.repository.BorrowedBookCopyRepo;
import jwl.fpt.repository.BorrowerTicketRepo;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.util.Constant;
import jwl.fpt.util.Helper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.*;

/**
 * Created by Entaard on 1/29/17.
 */
@Service
public class BookBorrowService implements IBookBorrowService {
    private final String principalName = FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;

    @Autowired
    private BorrowerTicketRepo borrowerTicketRepo;
    @Autowired
    private BorrowedBookCopyRepo borrowedBookCopyRepo;
    @Autowired
    private BookCopyRepo bookCopyRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FindByIndexNameSessionRepository sessionRepository;

    private List<BorrowCart> borrowCarts = new ArrayList<>();

    @Override
    public boolean initBorrowSession(HttpServletRequest request, BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        String iBeaconId = borrowerDto.getIBeaconId();
        String userId = borrowerDto.getUserId();
        HttpSession session = request.getSession(true);
        session.setAttribute(Constant.SESSION_BORROWER, userId);
        session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, iBeaconId);
        session.setMaxInactiveInterval(Constant.SESSION_INIT_TIMEOUT);

        return true;
    }

    /*
    It seems that Spring's SessionRepository cannot setAttribute to the session,
    or the attribute will not be saved in the database.
    -> Spring team recommends: only interact with normal HttpSession.

    SOLUTION: we init 1 session to hold the borrower id, then create another session to
    hold the real transaction of the borrower and the copies.
     */
    @Override
    public RfidDtoList addCopiesToSession(HttpServletRequest request, RfidDtoList rfidDtoList) {
        // TODO: Add necessary validations.
        HttpSession currentSession = request.getSession(false);
        if (currentSession != null) {
            RfidDtoList currentRfidDtoList = (RfidDtoList) currentSession.getAttribute(Constant.SESSION_PENDING_COPIES);
            if (currentRfidDtoList != null) {
                rfidDtoList.getRfids().addAll(currentRfidDtoList.getRfids());
                currentSession.setAttribute(Constant.SESSION_PENDING_COPIES, rfidDtoList);
                return  rfidDtoList;
            }
        }

        Session initSession = getSessionByPrincipalName(rfidDtoList.getIbeaconId());
        if (initSession == null) {
            return null;
        }

        createTransactionalSession(request, initSession, rfidDtoList);

//        sessionRepository.delete(initSession.getId());

        return rfidDtoList;
    }

    @Override
    @Transactional
    public List<BorrowedBookCopyDto> checkoutSession(HttpServletRequest request, String userId) {
        // TODO: Add necessary validations.
        Session transactionalSession = getSessionByPrincipalName(userId);
        List<BorrowedBookCopyDto> result = saveBorrowedCopies(transactionalSession);

        // TODO: Call deleteBorrowerTicket.
        // This function is commented, so that the ticket can be tested many times.
//        deleteBorrowerTicket(userId);

        sessionRepository.delete(transactionalSession.getId());

        return result;
    }

    @Override
    public BorrowerDto initBorrowCart(BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        String ibeaconId = borrowerDto.getIBeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        if (borrowCart != null) {
            return borrowerDto;
        }

        borrowCart = new BorrowCart();
        borrowCart.setIbeaconId(ibeaconId);
        borrowCart.setUserId(borrowerDto.getUserId());
        borrowCarts.add(borrowCart);
        return borrowerDto;
    }

    @Override
    public RfidDtoList addCopiesToCart(RfidDtoList rfidDtoList) {
        // TODO: Add necessary validations.
        String ibeaconId = rfidDtoList.getIbeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        if (borrowCart == null) {
            return null;
        }
        Set<String> rfids = borrowCart.getRfids();
        if (rfids == null || rfids.isEmpty()) {
            borrowCart.setRfids(rfidDtoList.getRfids());
        } else {
            borrowCart.getRfids().addAll(rfidDtoList.getRfids());
        }

        rfidDtoList.setRfids(borrowCart.getRfids());
        return rfidDtoList;
    }

    @Override
    public List<BorrowedBookCopyDto> checkoutCart(BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        String ibeaconId = borrowerDto.getIBeaconId();
        String userId = borrowerDto.getUserId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        if (borrowCart == null || !borrowCart.getUserId().equals(userId)) {
            return null;
        }

        List<BorrowedBookCopyDto> borrowedBookCopyDtos = saveBorrowCart(borrowCart);

        borrowCarts.remove(borrowCart);

        return borrowedBookCopyDtos;
    }

    private List<BorrowedBookCopyDto> saveBorrowCart(BorrowCart borrowCart) {
        Set<String> rfids = borrowCart.getRfids();
        if (rfids == null || rfids.isEmpty()) {
            return new ArrayList<>();
        }

        rfids.remove(null);
        List<BookCopyEntity> bookCopyEntities = bookCopyRepo.findAll(rfids);
        String userId = borrowCart.getUserId();
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = createBorrowedBookCopyEntities(bookCopyEntities, userId);
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

    private BorrowCart getCartByIbeaconId(String ibeaconId) {
        // TODO: check expire date.
        if (borrowCarts == null || borrowCarts.isEmpty()) {
            return null;
        }
        if (ibeaconId == null || ibeaconId.isEmpty()) {
            return null;
        }

        for (BorrowCart borrowCart :
                borrowCarts) {
            if (borrowCart.getIbeaconId().equals(ibeaconId)) {
                return borrowCart;
            }
        }

        return null;
    }

    private List<BorrowedBookCopyDto> saveBorrowedCopies(Session session) {
        // TODO: Add necessary validations.
        RfidDtoList rfidDtoList = session.getAttribute(Constant.SESSION_PENDING_COPIES);
        if (rfidDtoList == null) {
            return null;
        }
        Set<String> rfids = rfidDtoList.getRfids();
        rfids.remove(null);
        List<BookCopyEntity> bookCopyEntities = bookCopyRepo.findAll(rfids);

        String userId = session.getAttribute(Constant.SESSION_BORROWER);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = createBorrowedBookCopyEntities(bookCopyEntities, userId);
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

    private Session getSessionByPrincipalName(String principalValue) {
        Map<String, Session> sessionMap = sessionRepository
                .findByIndexNameAndIndexValue(principalName, principalValue);
        if (sessionMap.isEmpty()) {
            return null;
        }

        Map.Entry firstEntry = sessionMap.entrySet().iterator().next();

        return (Session) firstEntry.getValue();
    }

    private void createTransactionalSession(HttpServletRequest request,
                                            Session initSession, RfidDtoList rfidDtoList) {
        String userId = initSession.getAttribute(Constant.SESSION_BORROWER);
        HttpSession transactionalSession = request.getSession(true);

        transactionalSession.setAttribute(principalName, userId);
        transactionalSession.setAttribute(Constant.SESSION_BORROWER, userId);
        transactionalSession.setAttribute(Constant.SESSION_PENDING_COPIES, rfidDtoList);
        transactionalSession.setMaxInactiveInterval(Constant.SESSION_TRANSACT_TIMEOUT);
    }

    // TODO: Will be used later.
    private void deleteBorrowerTicket(String userId) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUserId(userId);
        BorrowerTicketEntity borrowerTicketEntity = borrowerTicketRepo
                .findByAccountAndDeleteDateIsNull(accountEntity);
        borrowerTicketEntity.setDeleteDate(new Date(Calendar.getInstance().getTimeInMillis()));
    }

    private List<BorrowedBookCopyEntity> createBorrowedBookCopyEntities(List<BookCopyEntity> bookCopyEntities,
                                                                        String userId) {
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = new ArrayList<>();

        for (BookCopyEntity bookCopyEntity:
                bookCopyEntities) {
            BookEntity bookEntity = bookCopyEntity.getBook();
            BookTypeEntity bookTypeEntity = bookEntity.getBookType();
            BorrowedBookCopyEntity entity = new BorrowedBookCopyEntity();

            entity.setAccount(userId);
            entity.setBookCopy(bookCopyEntity);
            entity.setBorrowedDate(new Date(Calendar.getInstance().getTimeInMillis()));
            Date deadline = Helper.GetDateAfter(entity.getBorrowedDate(), bookTypeEntity.getBorrowLimitDays());
            entity.setDeadlineDate(deadline);
            entity.setExtendNumber(0);

            borrowedBookCopyEntities.add(entity);
        }
        return borrowedBookCopyEntities;
    }
}

package jwl.fpt.service.imp.BookBorrowService;

import jwl.fpt.entity.*;
import jwl.fpt.model.BorrowCart;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;
import jwl.fpt.repository.AccountRepository;
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
    @Autowired
    private AccountRepository accountRepository;

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
    public RestServiceModel<BorrowerDto> initBorrowCart(BorrowerDto borrowerDto, boolean isLibrarian) {
        RestServiceModel<BorrowerDto> result = new RestServiceModel<>();
        boolean validInput = BookBorrowServiceValidator
                .validateBorrowerDtoForInit(borrowerDto, accountRepository, isLibrarian);
        if (!validInput) {
            result.setFailData(null, "Initiate borrow cart failed. Please contact librarian!");
            return result;
        }

        String ibeaconId = borrowerDto.getIBeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        result = checkToInitBorrowCart(borrowerDto, borrowCart);
        return result;
    }

    @Override
    public RestServiceModel<RfidDtoList> addCopiesToCart(RfidDtoList rfidDtoList) {
        // TODO: Add necessary validations.
        RestServiceModel<RfidDtoList> result = new RestServiceModel<>();
        boolean validInput = BookBorrowServiceValidator.validateRfidDtoList(rfidDtoList);
        if (!validInput) {
            result.setFailData(
                    null,
                    "Add books failed! Please contact librarian!",
                    "Add books failed!");
            return result;
        }

        String ibeaconId = rfidDtoList.getIbeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        result = checkToAddCopiesToBorrowCart(rfidDtoList, borrowCart);
        return result;
    }

    @Override
    public RestServiceModel<RfidDtoList> addCopyToCart(RfidDto rfidDto) {
        RestServiceModel<RfidDtoList> result = new RestServiceModel<>();
        boolean validInput = BookBorrowServiceValidator.validateRfidDto(rfidDto);
        if (!validInput) {
            result.setFailData(
                    null,
                    "Add book failed! Please contact librarian!",
                    "Add book failed!");
            return result;
        }

        RfidDtoList rfidDtoList = new RfidDtoList();
        rfidDtoList.setIbeaconId(rfidDto.getIbeaconId());
        Set<String> rfids = new HashSet<>();
        rfids.add(rfidDto.getRfid());
        rfidDtoList.setRfids(rfids);
        return addCopiesToCart(rfidDtoList);
    }

    // Changing method.
    @Override
    @Transactional
    public List<BorrowedBookCopyDto> checkoutCart(BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        String ibeaconId = borrowerDto.getIBeaconId();
        String userId = borrowerDto.getUserId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        if (borrowCart == null || !borrowCart.getUserId().equals(userId)) {
            return null;
        }

        List<BorrowedBookCopyDto> borrowedBookCopyDtos = saveBorrowCart(borrowCart);
        accountRepository.setStatus(false, userId);

        borrowCarts.remove(borrowCart);

        return borrowedBookCopyDtos;
    }


//    @Override
//    @Transactional
//    public RestServiceModel<List<BorrowedBookCopyDto>> checkoutCart(BorrowerDto borrowerDto) {
//        RestServiceModel<List<BorrowedBookCopyDto>> result = new RestServiceModel<>();
//        boolean validInput = BookBorrowServiceValidator.validateBorrowerDtoForCheckout(borrowerDto);
//        if (!validInput) {
//            result.setFailData(null, "Invalid userId or ibeaconId!");
//            return result;
//        }
//
//        String userId = borrowerDto.getUserId();
//        String ibeaconId = borrowerDto.getIBeaconId();
//
//        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
//        boolean userStatus = accountRepository.getStatus(userId);
//
//        // why save books to cart before saving to DB?
//        // what if checkout fail -> book saved in cart is not added to DB?
//        // safer: save book when scanned (?) -> checkout doesn't need to save books to DB
//        // Book scanned successfully -> save to DB
//        // Scan saved book again -> nothing, no error
//        // Scan another book failed -> alarm for only that book. Don't need to revert saved book
//
//        // user is in library
//        // find cart
//        // if dont have, normal checkout
//        // if have, save cart
//
//
//        // if found cart, but invalid userId ?
//        // a1: find some way to notice librarian
//
//
//        // a2: 1 borrower can only init checkout if the former borrower checked out
//        // how to know when a user is successfully checked out?
//
//
//        // a3: no cart. Scanned books are added directly to db. Rework everything!
//
//
//
//
//
//
//        if (borrowCart == null || !borrowCart.getUserId().equals(userId)) {
//            return null;
//        }
//
//        List<BorrowedBookCopyDto> borrowedBookCopyDtos = saveBorrowCart(borrowCart);
//        accountRepository.setStatus(false, userId);
//
//        borrowCarts.remove(borrowCart);
//
//        return null;
//    }

    @Override
    public List<BorrowedBookCopyDto> getBorrowingBookByUserId(AccountDto accountDto) {
        String userId = accountDto.getUserId();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUserId(userId);
        List<BorrowedBookCopyEntity> bookCopyEntities = borrowedBookCopyRepo.findByAccountAndReturnDateIsNull(accountEntity);
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = new ArrayList<>();
        for (BorrowedBookCopyEntity entity: bookCopyEntities) {
            BorrowedBookCopyDto dto = modelMapper.map(entity, BorrowedBookCopyDto.class);
            borrowedBookCopyDtos.add(dto);
        }
        return borrowedBookCopyDtos;
    }

    @Override
    public List<BorrowedBookCopyDto> deleteBorrowingCopy(BorrowedBookCopyDto borrowedBookCopyDto) {
        Integer borrowedBookCopyId = borrowedBookCopyDto.getId();
        String userId = borrowedBookCopyDto.getAccountUserId();

        if (borrowedBookCopyId == null || userId == null) {
            return null;
        }

        borrowedBookCopyRepo.deleteByUserIdAndBorrowedCopyId(userId, borrowedBookCopyId);

        // Return list of borrowing books.
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUserId(userId);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities =
                borrowedBookCopyRepo.findByAccountAndReturnDateIsNull(accountEntity);
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = new ArrayList<>();
        for (BorrowedBookCopyEntity entity: borrowedBookCopyEntities) {
            BorrowedBookCopyDto dto = modelMapper.map(entity, BorrowedBookCopyDto.class);
            borrowedBookCopyDtos.add(dto);
        }
        return borrowedBookCopyDtos;
    }

    @Override
    @Transactional
    public BorrowedBookCopyDto saveCopyToDatabase(RfidDto rfidDto) {
        String ibeaconId = rfidDto.getIbeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        if (borrowCart == null) {
            return null;
        }

        String userId = borrowCart.getUserId();
        if (userId == null) {
            return null;
        }
        BookCopyEntity bookCopyEntity = bookCopyRepo.findAvailableCopy(rfidDto.getRfid());
        if (bookCopyEntity == null) {
            return null;
        }

        List<BookCopyEntity> bookCopyEntities = new ArrayList<>();
        bookCopyEntities.add(bookCopyEntity);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = createBorrowedBookCopyEntities(bookCopyEntities, userId);
        borrowedBookCopyEntities = borrowedBookCopyRepo.save(borrowedBookCopyEntities);
        if (borrowedBookCopyEntities.isEmpty()) {
            return null;
        }
        BorrowedBookCopyDto borrowedBookCopyDto = modelMapper.map(borrowedBookCopyEntities.get(0), BorrowedBookCopyDto.class);

        return borrowedBookCopyDto;
    }

    private List<BorrowedBookCopyDto> saveBorrowCart(BorrowCart borrowCart) {
        Set<String> rfids = borrowCart.getRfids();
        if (rfids == null || rfids.isEmpty()) {
            return new ArrayList<>();
        }
        rfids.remove(null);

        List<BookCopyEntity> bookCopyEntities = bookCopyRepo.findAvailableCopies(rfids);
        if (bookCopyEntities == null || bookCopyEntities.size() != rfids.size()) {
            return null;
        }

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

    private void createNewBorrowCart(BorrowerDto borrowerDto) {
        BorrowCart borrowCart = new BorrowCart();
        borrowCart.setIbeaconId(borrowerDto.getIBeaconId());
        borrowCart.setUserId(borrowerDto.getUserId());
        borrowCarts.add(borrowCart);
    }

    private RestServiceModel<BorrowerDto> checkToInitBorrowCart(BorrowerDto borrowerDto, BorrowCart borrowCart) {
        RestServiceModel<BorrowerDto> result = new RestServiceModel<>();

        if (borrowCart != null) {
            // ibeacon already initiated its borrow cart.
            result.setSuccessData(borrowerDto, "Please scan your books.");
            return result;
        }

        createNewBorrowCart(borrowerDto);
        result.setSuccessData(borrowerDto, "You can scan book now.");
        return result;
    }

    private RestServiceModel<RfidDtoList> checkToAddCopiesToBorrowCart(RfidDtoList rfidDtoList, BorrowCart borrowCart) {
        RestServiceModel<RfidDtoList> result = new RestServiceModel<>();

        if (borrowCart == null) {
            result.setFailData(
                    null,
                    "Borrow cart not found! Please wait for other to complete checkout!");
            return result;
        }

        Set<String> rfids = borrowCart.getRfids();
        if (rfids == null || rfids.isEmpty()) {
            borrowCart.setRfids(rfidDtoList.getRfids());
        } else {
            borrowCart.getRfids().addAll(rfidDtoList.getRfids());
        }
        rfidDtoList.setRfids(borrowCart.getRfids());
        result.setSuccessData(
                rfidDtoList,
                "Book was added successfully!",
                "Book added!");
        return result;
    }
}

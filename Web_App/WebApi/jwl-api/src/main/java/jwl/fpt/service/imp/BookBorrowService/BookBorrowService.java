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
                .validateBorrowerDto(borrowerDto, accountRepository, isLibrarian);
        if (!validInput) {
            result.setFailData(null, "Initiate borrow cart failed. Please contact librarian!");
            return result;
        }

        String ibeaconId = borrowerDto.getIBeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        result = checkToInitBorrowCart(borrowerDto, borrowCart, isLibrarian);
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

    @Override
    @Transactional
    public RestServiceModel<List<BorrowedBookCopyDto>> checkoutCart(BorrowerDto borrowerDto, boolean isLibrarian) {
        RestServiceModel<List<BorrowedBookCopyDto>> result = new RestServiceModel<>();
        String msg = isLibrarian? "" : "Please contact librarian!";

        boolean validInput = BookBorrowServiceValidator
                .validateBorrowerDto(borrowerDto, accountRepository, isLibrarian);
        if (!validInput) {
            result.setFailData(null, "Checkout failed! " + msg);
            return result;
        }

        String ibeaconId = borrowerDto.getIBeaconId();
        String userId = borrowerDto.getUserId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        if (borrowCart == null || !userId.equals(borrowCart.getUserId())) {
            result.setFailData(null, "Check out failed! " + msg);
            return result;
        }

        result = saveBorrowCart(borrowCart);

        if (result.getCode().equals("200")) {
            accountRepository.setStatus(false, userId);
            borrowCarts.remove(borrowCart);
        }

        //TODO: Fail check out, what to do?

        return result;
    }

    @Override
    public RestServiceModel<Set<String>> cancelAddingCopies(BorrowerDto borrowerDto) {
        RestServiceModel<Set<String>> result = new RestServiceModel<>();

        boolean validInput = BookBorrowServiceValidator
                .validateBorrowerDto(borrowerDto, accountRepository, true);
        if (!validInput) {
            result.setFailData(null, "Checkout failed!");
            return result;
        }

        String ibeaconId = borrowerDto.getIBeaconId();
        String userId = borrowerDto.getUserId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        if (borrowCart == null || !userId.equals(borrowCart.getUserId())) {
            result.setFailData(null, "Check out failed!");
            return result;
        }


        accountRepository.setStatus(false, userId);
        result.setSuccessData(borrowCart.getRfids(), "Cancel successfully!");
        borrowCarts.remove(borrowCart);

        return result;
    }

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
    public RestServiceModel<BorrowedBookCopyDto> addCopyToCartByLibrarian(RfidDto rfidDto) {
        // TODO: later change to use librarian's id instead of ibeaconId.
        RestServiceModel<BorrowedBookCopyDto> result = new RestServiceModel<>();
        boolean validInput = BookBorrowServiceValidator.validateRfidDto(rfidDto);
        if (!validInput) {
            result.setFailData(
                    null,
                    "UserId or ibeaconId is invalid",
                    "Please recheck user ID!");
            return result;
        }

        String rfid = rfidDto.getRfid();
        String ibeaconId = rfidDto.getIbeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        RestServiceModel checkFoundCart = BookBorrowServiceValidator
                .validateFoundBorrowCart(borrowCart, true);
        if (checkFoundCart != null) {
            return checkFoundCart;
        }

        Set<String> cartRfids = borrowCart.getRfids();
        if (cartRfids != null && cartRfids.contains(rfid)) {
            result.setFailData(
                    null,
                    "The book copy " + rfid + " had already been added.",
                    "Book's just added!");
            return result;
        }

        BookCopyEntity bookCopyEntity = bookCopyRepo.findAvailableCopy(rfidDto.getRfid());
        if (bookCopyEntity == null) {
            rfid = bookCopyRepo.checkRfid(rfidDto.getRfid());

            if (rfid != null) {
                result.setFailData(
                        null,
                        "The book copy " + rfid + " had already been borrowed.",
                        "Book added before!");
            } else {
                result.setFailData(
                        null,
                        "Invalid book copy rfid!",
                        "Invalid book!");
            }
            return result;
        }

        Set<String> rfids = new HashSet<>();
        rfids.add(rfid);
        addCopiesToBorrowCart(rfids, borrowCart);

        List<BookCopyEntity> bookCopyEntities = new ArrayList<>();
        bookCopyEntities.add(bookCopyEntity);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities =
                createBorrowedBookCopyEntities(bookCopyEntities, borrowCart.getUserId());
        BorrowedBookCopyDto borrowedBookCopyDto = modelMapper
                .map(borrowedBookCopyEntities.get(0), BorrowedBookCopyDto.class);
        result.setSuccessData(
                borrowedBookCopyDto,
                "Book copy is saved successfully!",
                "Book added!");

        return result;
    }

    private RestServiceModel<List<BorrowedBookCopyDto>> saveBorrowCart(BorrowCart borrowCart) {
        RestServiceModel<List<BorrowedBookCopyDto>> result = new RestServiceModel<>();
        Set<String> rfids = borrowCart.getRfids();
        if (rfids == null || rfids.isEmpty()) {
            result.setSuccessData(null, "Checked out successfully! No book added.");
            return result;
        }
        rfids.remove(null);

        List<BookCopyEntity> bookCopyEntities = bookCopyRepo.findAvailableCopies(rfids);
        // the books with those rfids are not available, or invalid
        if (bookCopyEntities == null || bookCopyEntities.isEmpty()) {
            result.setSuccessData(null, "Checked out successfully! No book added.");
            return result;
        }

        String userId = borrowCart.getUserId();
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = createBorrowedBookCopyEntities(bookCopyEntities, userId);
        borrowedBookCopyEntities = borrowedBookCopyRepo.save(borrowedBookCopyEntities);
        if (borrowedBookCopyEntities.isEmpty()) {
            result.setFailData(null, "Check out failed system! Please contact admin/librarian!");
            return result;
        }

        List<BorrowedBookCopyDto> borrowedBookCopyDtos = new ArrayList<>();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                borrowedBookCopyEntities) {
            BorrowedBookCopyDto dto = modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
            borrowedBookCopyDtos.add(dto);
        }

        result.setSuccessData(borrowedBookCopyDtos, "Checked out with book(s) successfully!");
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

    private RestServiceModel<BorrowerDto> checkToInitBorrowCart(BorrowerDto borrowerDto,
                                                                BorrowCart borrowCart,
                                                                boolean isLibrarian) {
        RestServiceModel<BorrowerDto> result = new RestServiceModel<>();
        String userId = borrowerDto.getUserId();

        // ibeacon already initiated its borrow cart.
        if (borrowCart != null && !isLibrarian) {
            String userIdInCart = borrowCart.getUserId();

            // check userId in cart.
            if (userId.equals(userIdInCart)) {
                result.setSuccessData(borrowerDto, "Please scan your books.");
            } else {
                result.setFailData(null, "Please wait for other to complete checkout!");
            }
            return result;
        }

        if (borrowCart != null && isLibrarian) {
            borrowCarts.remove(borrowCart);
        }

        createNewBorrowCart(borrowerDto);
        result.setSuccessData(borrowerDto, "You can scan book now.");
        return result;
    }

    private RestServiceModel<RfidDtoList> checkToAddCopiesToBorrowCart(RfidDtoList rfidDtoList,
                                                                       BorrowCart borrowCart) {
        RestServiceModel<RfidDtoList> result = new RestServiceModel<>();
        RestServiceModel checkFoundCart = BookBorrowServiceValidator
                .validateFoundBorrowCart(borrowCart, false);
        if (checkFoundCart != null) {
            return checkFoundCart;
        }

        Set<String> rfids = addCopiesToBorrowCart(rfidDtoList.getRfids(), borrowCart);
        rfidDtoList.setRfids(rfids);

        result.setSuccessData(
                rfidDtoList,
                "Book was added successfully!",
                "Book added!");
        return result;
    }

    private Set<String> addCopiesToBorrowCart(Set<String> rfids, BorrowCart borrowCart) {
        Set<String> cartRfids = borrowCart.getRfids();

        if (cartRfids == null || cartRfids.isEmpty()) {
            borrowCart.setRfids(rfids);
        } else {
            borrowCart.getRfids().addAll(rfids);
            rfids = borrowCart.getRfids();
        }
        return rfids;
    }
}

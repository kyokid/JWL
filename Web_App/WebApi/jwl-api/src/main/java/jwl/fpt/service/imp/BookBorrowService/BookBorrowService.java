package jwl.fpt.service.imp.BookBorrowService;

import jwl.fpt.entity.*;
import jwl.fpt.model.BorrowCart;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.BookCopyRepo;
import jwl.fpt.repository.BorrowedBookCopyRepo;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.util.Constant;
import jwl.fpt.util.Constant.SoundMessages;
import jwl.fpt.util.Helper;
import jwl.fpt.util.NotificationUtils;
import lombok.Data;
import org.joda.time.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.*;

import static jwl.fpt.util.Constant.DAY_OF_DEADLINE;
import static jwl.fpt.util.Constant.DAY_REMAIN_DEADLINE;


/**
 * Created by Entaard on 1/29/17.
 */
@Service
public class BookBorrowService implements IBookBorrowService {
    private final String principalName = FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;

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
                return rfidDtoList;
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
                    SoundMessages.ERROR);
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
                    SoundMessages.ERROR);
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
        String msg = isLibrarian ? "" : "Please contact librarian!";

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
        for (BorrowedBookCopyEntity entity : bookCopyEntities) {
            BorrowedBookCopyDto dto = modelMapper.map(entity, BorrowedBookCopyDto.class);
            borrowedBookCopyDtos.add(dto);
        }
        return borrowedBookCopyDtos;
    }

    @Override
    public List<BorrowedBookCopyDto> deleteBorrowingCopy(BorrowedBookCopyDto borrowedBookCopyDto) {
        String borrowedBookCopyRfid = borrowedBookCopyDto.getBookCopyRfid();
        String userId = borrowedBookCopyDto.getAccountUserId();

        if (borrowedBookCopyRfid == null || userId == null || borrowedBookCopyRfid.isEmpty()) {
            return null;
        }

        borrowedBookCopyRepo.deleteByUserIdAndBorrowedCopyRfid(userId, borrowedBookCopyRfid);

        // Return list of borrowing books.
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUserId(userId);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities =
                borrowedBookCopyRepo.findByAccountAndReturnDateIsNull(accountEntity);
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = new ArrayList<>();
        for (BorrowedBookCopyEntity entity : borrowedBookCopyEntities) {
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
                    "Book has just been added!");
            return result;
        }

        BookCopyEntity bookCopyEntity = bookCopyRepo.findAvailableCopy(rfid);
        if (bookCopyEntity == null) {
            rfid = bookCopyRepo.checkRfid(rfid);

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

    @Override
    public RestServiceModel<Boolean> renewBorrowedBookCopy(String rfid) {
        RestServiceModel<Boolean> result = new RestServiceModel<>();

        if (rfid == null) {
            result.setFailData(null, "Sách bạn yêu cầu không có");
            return result;
        }
        BorrowedBookCopyEntity currentBook = borrowedBookCopyRepo.findByBookCopy_RfidAndReturnDateIsNull(rfid);
        int currentExtentNumber = currentBook.getExtendNumber();
        BookCopyEntity bookCopyEntity = currentBook.getBookCopy();
        BookTypeEntity bookTypeEntity = bookCopyEntity.getBook().getBookType();
        int maxExtend = bookTypeEntity.getExtendTimesLimit();
        if (currentExtentNumber == maxExtend) {
            result.setFailData(null, "Bạn không thể gia hạn sách do vượt quá số lần cho phép, vui lòng trả lại sách cho thư viện");
            return result;
        }
        //trả sách
        int resultUpdate = borrowedBookCopyRepo.updateReturnDate(new Date(Calendar.getInstance().getTimeInMillis()), rfid);
        if (resultUpdate != 0) {
            //get information sách mới trả

            String userId = currentBook.getAccount().getUserId();
            int rootId;
            if (currentExtentNumber == 0) {
                rootId = currentBook.getId();
            } else {
                rootId = currentBook.getRootId();
            }

            // insert borrowedbookcopy mới
            List<BorrowedBookCopyEntity> borrowedBookCopyEntities = new ArrayList<>();
            BorrowedBookCopyEntity entity = new BorrowedBookCopyEntity();
            entity.setAccount(userId);
            entity.setBookCopy(bookCopyEntity);
            entity.setBorrowedDate(new Date(Calendar.getInstance().getTimeInMillis()));
            Date deadline = Helper.GetDateAfter(entity.getBorrowedDate(), bookTypeEntity.getBorrowLimitDays());
            entity.setDeadlineDate(deadline);
            entity.setExtendNumber(currentExtentNumber + 1);
            entity.setRootId(rootId);
            borrowedBookCopyEntities.add(entity);
            //save to db
            borrowedBookCopyEntities = borrowedBookCopyRepo.save(borrowedBookCopyEntities);

            List<BorrowedBookCopyDto> borrowedBookCopyDtos = new ArrayList<>();
            for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                    borrowedBookCopyEntities) {
                BorrowedBookCopyDto dto = modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
                borrowedBookCopyDtos.add(dto);
            }
            result.setSuccessData(true, "Bạn đã gia hạn thành công");
        }

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

    private List<BorrowedBookCopyEntity> createBorrowedBookCopyEntities(List<BookCopyEntity> bookCopyEntities,
                                                                        String userId) {
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = new ArrayList<>();

        for (BookCopyEntity bookCopyEntity :
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

        Set<String> inputRfids = rfidDtoList.getRfids();
        // Case only one book is added => check to response correct sound message.
        if (inputRfids != null && inputRfids.size() == 1) {
            String rfid = inputRfids.iterator().next();

            Set<String> cartRfids = borrowCart.getRfids();
            if (cartRfids != null && cartRfids.contains(rfid)) {
                result.setFailData(
                        null,
                        "The book copy " + rfid + " had already been added.",
                        SoundMessages.ALREADY);
                return result;
            }

            BookCopyEntity bookCopyEntity = bookCopyRepo.findAvailableCopy(rfid);
            if (bookCopyEntity == null) {
                rfid = bookCopyRepo.checkRfid(rfid);

                if (rfid != null) {
                    result.setFailData(
                            null,
                            "The book copy " + rfid + " had already been borrowed.",
                            SoundMessages.ALREADY);
                } else {
                    result.setFailData(
                            null,
                            "Invalid book copy rfid!",
                            SoundMessages.ERROR);
                }
                return result;
            }
        }

        Set<String> rfids = addCopiesToBorrowCart(inputRfids, borrowCart);
        rfidDtoList.setRfids(rfids);

        result.setSuccessData(
                rfidDtoList,
                "Book was added successfully!",
                SoundMessages.OK);
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

    public void checkBorrowingBookCopyDeadline() {
        Logger logger = LoggerFactory.getLogger(getClass());

        logger.info("The check deadline has begun...");
        int count = 0;
        List<BorrowedBookCopyDto> result = new ArrayList<>();
        LocalDate currentLocal = new LocalDate();
        LocalDate deadLineLocal;
        Days diffDate;
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = borrowedBookCopyRepo.findByReturnDateIsNull();
        List<BorrowedBookCopyEntity> borrowedBook3DayDeadline = new ArrayList<>();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                borrowedBookCopyEntities) {
            deadLineLocal = LocalDate.fromDateFields(borrowedBookCopyEntity.getDeadlineDate());
            diffDate = Days.daysBetween(currentLocal, deadLineLocal);
            logger.info("Duration is {} of {} book", diffDate.getDays(), borrowedBookCopyEntity.getId());

            // deadline - current = 3 thì push notiviện
            // Todo: push notification 2
            if (diffDate.getDays() == DAY_REMAIN_DEADLINE) {
                borrowedBook3DayDeadline.add(borrowedBookCopyEntity);
                logger.info("còn 3 ngày nữa là đến deadline sách: {} ", borrowedBookCopyEntity.getBookCopy().getBook().getTitle());
                count++;
            } else if (diffDate.getDays() == DAY_OF_DEADLINE) {
                logger.info("sách {} đã hết hạn, vui lòng trả lại thư ", borrowedBookCopyEntity.getBookCopy().getBook().getTitle());
                count++;
            }
        }
        if (count == 0) {
            logger.info("không có sách nào phải noti");
        } else if (borrowedBook3DayDeadline.size() != 0){
            List<AccountDto> listUser = new ArrayList<>();

            for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                    borrowedBook3DayDeadline) {
                AccountDto accountDto = modelMapper.map(borrowedBookCopyEntity.getAccount(), AccountDto.class);
//                borrowedBookCopyEntity.getAccount().getGoogleToken()
                if (!listUser.contains(accountDto)) {
                    listUser.add(accountDto);
                }
                BorrowedBookCopyDto dto = modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
                result.add(dto);
            }
            for (AccountDto dto : listUser) {
                List<BorrowedBookCopyDto> bookDeadlines = new ArrayList<>();

                for (BorrowedBookCopyDto borrowedBookCopyDto : result) {
                    if (dto.getUserId().equals(borrowedBookCopyDto.getAccountUserId())) {
                        bookDeadlines.add(borrowedBookCopyDto);
                    }
                }
                NotificationUtils.pushNotificationDeadline(bookDeadlines, dto.getGoogleToken());
                logger.info("Gửi noti cho thằng {} với số sách {} ", dto.getUserId(), bookDeadlines.size());
            }
            logger.info("Có {} cuốn sách chuẩn bị tới deadline", count);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Error while executing sample job", e);
        } finally {
            logger.info("Sample job has finished...");
        }
    }

}

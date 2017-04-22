package jwl.fpt.service.imp.BookBorrowService;

import jwl.fpt.entity.*;
import jwl.fpt.model.BorrowCart;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.BookCopyRepo;
import jwl.fpt.repository.BorrowedBookCopyRepo;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.service.IBookService;
import jwl.fpt.util.Constant;
import jwl.fpt.util.Constant.SoundMessages;
import jwl.fpt.util.Helper;
import jwl.fpt.util.NotificationUtils;
import org.joda.time.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.*;

import static jwl.fpt.util.Constant.*;


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
    @Autowired
    private IBookService bookService;

    @Value("${library.fine.cost}")
    private Integer fineCost;

    private List<BorrowCart> borrowCarts = new ArrayList<>();

    // Future plan
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
    // Future plan
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

    // Future plan
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
            result.setFailData(null, "Có lỗi xảy ra. Vui lòng liên hệ thủ thư!");
            return result;
        }

        String ibeaconId = borrowerDto.getIBeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        result = checkToInitBorrowCart(borrowerDto, borrowCart, isLibrarian);
        return result;
    }

    // Future plan
    @Override
    public RestServiceModel<RfidDtoList> scanCopiesToCart(RfidDtoList rfidDtoList) {
        // TODO: Add necessary validations.
        // TODO: check to conform business with scanCopyToCart
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
    public RestServiceModel<RfidDtoList> scanCopyToCart(RfidDto rfidDto) {
        RestServiceModel<RfidDtoList> result = new RestServiceModel<>();
        boolean validInput = BookBorrowServiceValidator.validateRfidDto(rfidDto);
        if (!validInput) {
            result.setFailData(
                    null,
                    "Add book failed! Please contact librarian!",
                    SoundMessages.ERROR);
            return result;
        }

        String ibeaconId = rfidDto.getIbeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        result = checkToAddCopyToBorrowCart(rfidDto, borrowCart);
        return result;
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
            BorrowedBookCopyDto.setBookStatusForOneDto(dto);
            dto.setFineCost(fineCost);
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
            BorrowedBookCopyDto.setBookStatusForOneDto(dto);
            borrowedBookCopyDtos.add(dto);
        }
        return borrowedBookCopyDtos;
    }

    @Override
    @Transactional
    public RestServiceModel<BorrowedBookCopyDto> scanCopyToCartByLibrarian(RfidDto rfidDto) {
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

        String inputRfid = rfidDto.getRfid();
        String ibeaconId = rfidDto.getIbeaconId();
        BorrowCart borrowCart = getCartByIbeaconId(ibeaconId);
        RestServiceModel checkFoundCart = BookBorrowServiceValidator
                .validateFoundBorrowCart(borrowCart, true);
        if (checkFoundCart != null) {
            return checkFoundCart;
        }
        int newBookLimit = borrowCart.getBookLimit();
        if (newBookLimit == 0) {
            result.setFailData(null,
                    "Borrower can not borrow anymore",
                    "Can not borrow anymore");
            return result;
        }
        newBookLimit -= 1;

        Set<String> cartRfids = borrowCart.getRfids();
        if (cartRfids != null && cartRfids.contains(inputRfid)) {
            result.setFailData(
                    null,
                    "The book copy " + inputRfid + " had already been added.",
                    "Book has just been added!");
            return result;
        }

        int newUsableBalance;
        BookCopyEntity bookCopyEntity = bookCopyRepo.findAvailableCopy(inputRfid);
        if (bookCopyEntity == null) {
            inputRfid = bookCopyRepo.checkRfid(inputRfid);

            if (inputRfid != null) {
                result.setFailData(
                        null,
                        "The book copy " + inputRfid + " had already been borrowed.",
                        "Book added before!");
            } else {
                result.setFailData(
                        null,
                        "Invalid book copy rfid!",
                        "Invalid book!");
            }
            return result;
        } else {
            int currentUsableBalance = borrowCart.getUsableBalance();
            newUsableBalance = calculateRemainUsableBalanceIfBorrowCopy(bookCopyEntity, currentUsableBalance);
            if (newUsableBalance < 0) {
                result.setFailData(
                        null,
                        "User " + borrowCart.getUserId() + " does not have enough to borrow copy " + inputRfid,
                        "Not enough money!");
                return result;
            }
        }

        addCopyToBorrowCart(inputRfid, newUsableBalance, borrowCart);

        List<BookCopyEntity> bookCopyEntities = new ArrayList<>();
        bookCopyEntities.add(bookCopyEntity);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities =
                createBorrowedBookCopyEntities(bookCopyEntities, borrowCart.getUserId());
        BorrowedBookCopyDto borrowedBookCopyDto = modelMapper
                .map(borrowedBookCopyEntities.get(0), BorrowedBookCopyDto.class);
        borrowCart.setBookLimit(newBookLimit);
        result.setSuccessData(
                borrowedBookCopyDto,
                "Book copy is saved successfully!",
                "Book added!");

        return result;
    }

    @Override
    public RestServiceModel<BorrowedBookCopyDto> renewBorrowedBookCopy(String rfid) {
        RestServiceModel<BorrowedBookCopyDto> result = new RestServiceModel<>();
        int resultUpdate;
        if (rfid == null) {
            result.setFailData(null, "Sách bạn yêu cầu không có.");
            return result;
        }
        BorrowedBookCopyEntity currentBook = borrowedBookCopyRepo.findByBookCopy_RfidAndReturnDateIsNull(rfid);
        int currentExtentNumber = currentBook.getExtendNumber();
        BookCopyEntity bookCopyEntity = currentBook.getBookCopy();
        BookTypeEntity bookTypeEntity = bookCopyEntity.getBook().getBookType();
        int maxExtend = bookTypeEntity.getExtendTimesLimit();
        if (currentExtentNumber == maxExtend) {
            String message = "Gia hạn sách "
                    + bookCopyEntity.getBook().getTitle()
                    + " không thành công. Bạn vui lòng mang sách tới thư viện để kiểm tra lại tình trạng của sách.";
            result.setFailData(null, message);
            return result;
        }
        //trả sách
        if (currentExtentNumber == 0) {
            resultUpdate = borrowedBookCopyRepo.updateReturnDateWhereExtendEquals0(new Date(Calendar.getInstance().getTimeInMillis()), rfid);
        } else {
            resultUpdate = borrowedBookCopyRepo.updateReturnDate(new Date(Calendar.getInstance().getTimeInMillis()), rfid);
        }
        if (resultUpdate != 0) {
            //get information sách mới trả
            int cautionMoney = currentBook.getCautionMoney();
            String userId = currentBook.getAccount().getUserId();
            int rootId;
            if (currentExtentNumber == 0) {
                rootId = currentBook.getId();
            } else {
                rootId = currentBook.getRootId();
            }
            Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
            Date newBorrowedDate;
            if (currentDate.before(currentBook.getDeadlineDate())) {
                newBorrowedDate = currentBook.getDeadlineDate();
            } else {
                newBorrowedDate = currentDate;
            }

            // insert borrowedbookcopy mới
            BorrowedBookCopyEntity entity = new BorrowedBookCopyEntity();
            entity.setAccount(userId);
            entity.setBookCopy(bookCopyEntity);
            entity.setBorrowedDate(newBorrowedDate);
            Date deadline = Helper.getDateAfter(currentBook.getDeadlineDate(), bookTypeEntity.getDaysPerExtend());
            entity.setDeadlineDate(deadline);
            entity.setExtendNumber(currentExtentNumber + 1);
            entity.setRootId(rootId);
            entity.setCautionMoney(cautionMoney);
            entity.setNotiStatus(null);
            //save to db
            entity = borrowedBookCopyRepo.save(entity);

            BorrowedBookCopyDto dto = modelMapper.map(entity, BorrowedBookCopyDto.class);
            String message = "Bạn đã gia hạn sách " + dto.getBookCopyBookTitle() + " thành công.";
            result.setSuccessData(dto, message);
        }

        return result;
    }

    @Override
    public RestServiceModel<List<BorrowedBookCopyDto>> getHistory(String userId) {
        RestServiceModel<List<BorrowedBookCopyDto>> result = new RestServiceModel<>();
        //1. get tat ca sach cua user khanhkt, co rootId is null and return date not null
        List<BorrowedBookCopyEntity> listKhongGiaHan = borrowedBookCopyRepo.findByUserIdAndRootIdNULL(userId);
        //2. convert list 1 to dto
        List<BorrowedBookCopyDto> listKhongGiaHanDTO = new ArrayList<>();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity : listKhongGiaHan) {
            BorrowedBookCopyDto borrowedBookCopyDto =
                    modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
            listKhongGiaHanDTO.add(borrowedBookCopyDto);
        }

        //3. get tat ca sach cua user khanhkt, co root id not null, return date not null * get last row.
        List<BorrowedBookCopyEntity> listGiaHanVaDaTra = borrowedBookCopyRepo.getListLast(userId);
        //4. convert list 3
        List<BorrowedBookCopyDto> listGiaHanVaDaTraDTO = new ArrayList<>();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity : listGiaHanVaDaTra) {
            BorrowedBookCopyDto borrowedBookCopyDto =
                    modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
            listGiaHanVaDaTraDTO.add(borrowedBookCopyDto);
        }
        //5. set borrow_date
        List<BorrowedBookCopyEntity> listFirstGiaHan = borrowedBookCopyRepo.getListFirst(userId);
        for (int i = 0; i < listGiaHanVaDaTraDTO.size(); i++) {
            listGiaHanVaDaTraDTO.get(i).setBorrowedDate(listFirstGiaHan.get(i).getBorrowedDate());
        }

        listKhongGiaHanDTO.addAll(listGiaHanVaDaTraDTO);
        for (BorrowedBookCopyDto dto : listKhongGiaHanDTO) {
            Date deadline = dto.getDeadlineDate();
            Date returnDate = dto.getReturnDate();
            long aaa = (deadline.getTime() - returnDate.getTime()) / MILISECOND_PER_DAYS;
            System.out.println("So ngay: " + aaa);
            if (aaa >= 0) {
                dto.setBookStatus(BOOK_STATUS_OK);
            } else {
                dto.setBookStatus((int) aaa);
            }

        }
        result.setData(listKhongGiaHanDTO);
        result.setSucceed(true);
        result.setTextMessage("Found " + listKhongGiaHanDTO.size() + " book(s).");
        return result;
    }

    private RestServiceModel<List<BorrowedBookCopyDto>> saveBorrowCart(BorrowCart borrowCart) {
        RestServiceModel<List<BorrowedBookCopyDto>> result = new RestServiceModel<>();
        Set<String> rfids = borrowCart.getRfids();
        if (rfids == null || rfids.isEmpty()) {
            result.setSuccessData(null, "Cảm ơn bạn đã sử dụng thư viện.");
            return result;
        }
        rfids.remove(null);

        List<BookCopyEntity> bookCopyEntities = bookCopyRepo.findAvailableCopies(rfids);
        // the books with those rfids are not available, or invalid
        if (bookCopyEntities == null || bookCopyEntities.isEmpty()) {
            result.setSuccessData(null, "Cảm ơn bạn đã sử dụng thư viện.");
            return result;
        }

        String userId = borrowCart.getUserId();
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = createBorrowedBookCopyEntities(bookCopyEntities, userId);
        borrowedBookCopyEntities = borrowedBookCopyRepo.save(borrowedBookCopyEntities);
        if (borrowedBookCopyEntities.isEmpty()) {
            result.setFailData(null, "Có lỗi xảy ra. Vui lòng liên hệ thủ thư!");
            return result;
        }

        List<BorrowedBookCopyDto> borrowedBookCopyDtos = new ArrayList<>();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                borrowedBookCopyEntities) {
            BorrowedBookCopyDto dto = modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
            dto.setBookStatus(BOOK_STATUS_OK);
            borrowedBookCopyDtos.add(dto);
        }

        result.setSuccessData(borrowedBookCopyDtos, "Mượn sách thành công! Vui lòng ấn để xem chi tiết.");
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

    private BorrowCart getCartByUserId(String userId) {
        // TODO: check expire date.
        if (borrowCarts == null || borrowCarts.isEmpty()) {
            return null;
        }

        for (BorrowCart borrowCart :
                borrowCarts) {
            if (borrowCart.getUserId().equals(userId)) {
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
            Date deadline = Helper.getDateAfter(entity.getBorrowedDate(), bookTypeEntity.getBorrowLimitDays());
            entity.setDeadlineDate(deadline);
            entity.setExtendNumber(0);
            entity.setCautionMoney(bookService.calculateCautionMoney(bookEntity));

            borrowedBookCopyEntities.add(entity);
        }
        return borrowedBookCopyEntities;
    }

    private void createNewBorrowCart(BorrowerDto borrowerDto) {
        BorrowCart borrowCart = new BorrowCart();
        borrowCart.setIbeaconId(borrowerDto.getIBeaconId());
        borrowCart.setUserId(borrowerDto.getUserId());
        borrowCart.setUsableBalance(calculateUsableBalanceFromDb(borrowerDto.getUserId()));
        borrowCart.setBookLimit(calculateBookLimit(borrowerDto.getUserId()));
        borrowCarts.add(borrowCart);
    }

    private int calculateBookLimit(String userId) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUserId(userId);
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities =
                borrowedBookCopyRepo.findByAccountAndReturnDateIsNull(accountEntity);
        int maxBook = accountRepository.findMaxNumberOfBooksByUserId(userId);
        int bookLimit = maxBook - borrowedBookCopyEntities.size();
        return bookLimit < 0 ? 0 : bookLimit;
    }

    private RestServiceModel<BorrowerDto> checkToInitBorrowCart(BorrowerDto borrowerDto,
                                                                BorrowCart borrowCart,
                                                                boolean isLibrarian) {
        RestServiceModel<BorrowerDto> result = new RestServiceModel<>();
        String userId = borrowerDto.getUserId();

        // ibeacon already initiated its borrow cart.
        if (borrowCart != null && !isLibrarian) {
            String userIdInCart = borrowCart.getUserId();
            boolean scanFailed = borrowCart.isScanFailed();

            // check userId in cart.
            // if still a same borrower
            if (userId.equals(userIdInCart)) {
                result.setSuccessData(borrowerDto, "Bạn có thể mượn sách rồi.");
                return result;
            }
            // if found another borrower, and the current borrower still checking out
            if (!scanFailed) {
                result.setFailData(null, "Vui lòng chờ tới lượt bạn!");
                return result;
            }
            // if found another borrower, and the current borrower failed (alarm raised)
            BorrowerDto currentFailedBorrower = new BorrowerDto(borrowerDto.getIBeaconId(), borrowCart.getUserId());
            checkoutCart(currentFailedBorrower, false);
        }

        if (borrowCart != null && isLibrarian) {
            borrowCarts.remove(borrowCart);
        }

        createNewBorrowCart(borrowerDto);
        result.setSuccessData(borrowerDto, "Bạn có thể mượn sách.");
        return result;
    }

    private RestServiceModel<RfidDtoList> checkToAddCopiesToBorrowCart(RfidDtoList rfidDtoList,
                                                                       BorrowCart borrowCart) {
        // TODO: check usable_balance and update caution_money case many copies are scanned at the same time
        // TODO: check validation
        RestServiceModel<RfidDtoList> result = new RestServiceModel<>();
        RestServiceModel checkFoundCart = BookBorrowServiceValidator
                .validateFoundBorrowCart(borrowCart, false);
        if (checkFoundCart != null) {
            return checkFoundCart;
        }

        Set<String> inputRfids = rfidDtoList.getRfids();
        Set<String> rfids = addCopiesToBorrowCart(inputRfids, borrowCart);
        rfidDtoList.setRfids(rfids);

        result.setSuccessData(
                rfidDtoList,
                "Book was added successfully!",
                SoundMessages.OK);
        return result;
    }

    private RestServiceModel<RfidDtoList> checkToAddCopyToBorrowCart(RfidDto rfidDto,
                                                                     BorrowCart borrowCart) {
        RestServiceModel<RfidDtoList> result = new RestServiceModel<>();
        RestServiceModel checkFoundCart = BookBorrowServiceValidator
                .validateFoundBorrowCart(borrowCart, false);
        if (checkFoundCart != null) {
            return checkFoundCart;
        }
        if (borrowCart.getBookLimit() == 0) {
            borrowCart.setScanFailed(true);
            result.setFailData(null,
                    "Borrower can not borrow anymore",
                    SoundMessages.ERROR);
            return result;
        }
        int newBookLimit = borrowCart.getBookLimit() - 1;

        String inputRfid = rfidDto.getRfid();
        Set<String> cartRfids = borrowCart.getRfids();
        if (cartRfids != null && cartRfids.contains(inputRfid)) {
            result.setFailData(
                    null,
                    "The book copy " + inputRfid + " had already been added.",
                    SoundMessages.ALREADY);
            return result;
        }

        int newUsableBalance;
        BookCopyEntity bookCopyEntity = bookCopyRepo.findAvailableCopy(inputRfid);
        if (bookCopyEntity == null) {
            inputRfid = bookCopyRepo.checkRfid(inputRfid);

            if (inputRfid != null) {
                result.setFailData(
                        null,
                        "The book copy " + inputRfid + " had already been borrowed.",
                        SoundMessages.ALREADY);
            } else {
                borrowCart.setScanFailed(true);
                result.setFailData(
                        null,
                        "Invalid book copy rfid!",
                        SoundMessages.ERROR);
            }
            return result;
        } else {
            int currentUsableBalance = borrowCart.getUsableBalance();
            newUsableBalance = calculateRemainUsableBalanceIfBorrowCopy(bookCopyEntity, currentUsableBalance);
            if (newUsableBalance < 0) {
                borrowCart.setScanFailed(true);
                result.setFailData(
                        null,
                        "User " + borrowCart.getUserId() + " does not have enough to borrow copy " + inputRfid,
                        SoundMessages.ERROR);
                return result;
            }
        }

        Set<String> rfids = addCopyToBorrowCart(inputRfid, newUsableBalance, borrowCart);
        RfidDtoList rfidDtoList = new RfidDtoList();
        rfidDtoList.setRfids(rfids);
        rfidDtoList.setIbeaconId(rfidDto.getIbeaconId());
        borrowCart.setBookLimit(newBookLimit);

        result.setSuccessData(
                rfidDtoList,
                "Book was added successfully!",
                SoundMessages.OK);
        return result;
    }

    @Override
    public int calculateUsableBalanceFromDb(String userId) {
        AccountEntity accountEntity = accountRepository.findByUserId(userId);
        int usableBalance = accountEntity.getTotalBalance();
        if (usableBalance == 0) {
            return usableBalance;
        }

        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = (List<BorrowedBookCopyEntity>) accountEntity.getBorrowedBookCopies();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                borrowedBookCopyEntities) {
            int cautionMoney = borrowedBookCopyEntity.getCautionMoney();
            usableBalance -= cautionMoney;
        }
        return usableBalance;
    }

    @Override
    public void sendNotificationForLateDeadline() {
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = borrowedBookCopyRepo.findByNotiStatus(NEED_TO_PUSH_NOTIFICATION);
        List<AccountDto> listUser = new ArrayList<>();
        if (borrowedBookCopyEntities != null || borrowedBookCopyEntities.size() > 0) {
            //push notification
            for (BorrowedBookCopyEntity book : borrowedBookCopyEntities) {
                book.setNotiStatus(PUSHED_NOTIFICATION);
                book = borrowedBookCopyRepo.save(book);
                AccountDto accountDto = modelMapper.map(book.getAccount(), AccountDto.class);
                if (!listUser.contains(accountDto)) {
                    listUser.add(accountDto);
                }
            }
        }
        if (listUser.size() > 0) {
            for (AccountDto dto : listUser) {
                try {
                    // push noti
                    NotificationUtils.pushNotificationDeadline(dto.getGoogleToken(),
                            "Bạn có sách phải trả cho thư viện");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean updateUsableBalanceInBorrowCartOf(String userId, int difference) {
        BorrowCart borrowCart = getCartByUserId(userId);
        if (borrowCart == null) {
            return false;
        }

        int currentUsableBalance = borrowCart.getUsableBalance();
        borrowCart.setUsableBalance(currentUsableBalance + difference);
        return true;
    }

    @Override
    public List<BorrowCart> getListBorrowCart() {
        return borrowCarts;
    }

    private int calculateRemainUsableBalanceIfBorrowCopy(BookCopyEntity bookCopyEntity, int currentUsableBalance) {
        BookEntity bookEntity = bookCopyEntity.getBook();
        int neededCautionMoney = bookService.calculateCautionMoney(bookEntity);
        if (currentUsableBalance >= neededCautionMoney) {
            return currentUsableBalance - neededCautionMoney;
        }
        return -1;
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

    private Set<String> addCopyToBorrowCart(String rfid,
                                            int usableBalance,
                                            BorrowCart borrowCart) {
        Set<String> cartRfids = borrowCart.getRfids();
        cartRfids.add(rfid);
        borrowCart.setUsableBalance(usableBalance);

        return cartRfids;
    }

    @Override
    public void checkBorrowingBookCopyDeadline() throws UnsupportedEncodingException, NullPointerException {
        Logger logger = LoggerFactory.getLogger(getClass());

        logger.info("The check deadline has begun...");
        // ngày hiện tại
        LocalDate currentLocal = new LocalDate();
        // ngày deadline của book
        LocalDate deadLineLocal;
        // biến để so sánh ngày
        Days diffDate;
        // list sách đang mượn
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = borrowedBookCopyRepo.findByReturnDateIsNull();

        List<BorrowedBookCopyEntity> missDeadlineCopies = new ArrayList<>();

        List<BorrowedBookCopyEntity> lostCopies = new ArrayList<>();

        for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                borrowedBookCopyEntities) {
            deadLineLocal = LocalDate.fromDateFields(borrowedBookCopyEntity.getDeadlineDate());
            diffDate = Days.daysBetween(currentLocal, deadLineLocal);
            int diffDays = diffDate.getDays();
            if (borrowedBookCopyEntity.getNotiStatus() == null) {
                // deadline - current <= 3 thì push noti
                if (diffDays <= DAY_REMAIN_DEADLINE) {
                    borrowedBookCopyEntity.setNotiStatus(NEED_TO_PUSH_NOTIFICATION);
                    borrowedBookCopyEntity = borrowedBookCopyRepo.save(borrowedBookCopyEntity);
                }
            }

            int lateDaysLimit = borrowedBookCopyEntity.getBookCopy().getBook().getBookType().getLateDaysLimit();
            if (diffDays < 0 && diffDays >= -lateDaysLimit) {
                missDeadlineCopies.add(borrowedBookCopyEntity);
            } else if (diffDays < -lateDaysLimit) {
                lostCopies.add(borrowedBookCopyEntity);

            }
        }

        handlePenalty(missDeadlineCopies, false);
        handlePenalty(lostCopies, true);

        // TODO: push noti after fined user.

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error("Error while executing check deadline job", e);
        } finally {
            logger.info("Check deadline job has finished...");
        }
    }

    @Transactional
    private void handlePenalty(List<BorrowedBookCopyEntity> afterDeadlineCopies, boolean lostBook) {
        if (afterDeadlineCopies.size() == 0) {
            return;
        }

        List<AccountEntity> finedAccountEntities = new ArrayList<>();
        List<String> finedUserIds = new ArrayList<>();
        Iterator<BorrowedBookCopyEntity> iterator = afterDeadlineCopies.iterator();
        while (iterator.hasNext()) {
            BorrowedBookCopyEntity entity = iterator.next();
            AccountEntity finedAccountEntity = entity.getAccount();
            int totalBalance = finedAccountEntity.getTotalBalance();
            int cautionMoney = entity.getCautionMoney();

            if (totalBalance == 0 || cautionMoney == 0) {
                continue;
            }

            // check if the user is not already in the list
            String finedUserId = finedAccountEntity.getUserId();
            if (!finedUserIds.contains(finedUserId)) {
                finedUserIds.add(finedUserId);
                finedAccountEntities.add(finedAccountEntity);
            } else {
                // get the user that is currently in the list
                finedAccountEntity = finedAccountEntities.get(finedUserIds.indexOf(finedUserId));
            }
            if (lostBook) {
                totalBalance -= cautionMoney;
                lostBookProcess(finedUserId, entity);
                iterator.remove();
            } else {
                // in case the scheduler fails to run in some days, the server still calculates the right fine value to borrower.
                int daysInterval = calculateNumberOfLateDays(entity.getDeadlineDate());
                totalBalance -= fineCost * daysInterval;
                cautionMoney -= fineCost * daysInterval;
                entity.setCautionMoney(cautionMoney);

            }
            finedAccountEntity.setTotalBalance(totalBalance);


        }
        accountRepository.save(finedAccountEntities);
        borrowedBookCopyRepo.save(afterDeadlineCopies);
    }

    private int calculateNumberOfLateDays(Date deadline) {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        return Helper.getDaysInterval(deadline, currentDate);
    }

    private void lostBookProcess(String finedUserId, BorrowedBookCopyEntity borrowedBookCopyEntity) {
        String lostReasonMessage = finedUserId + " - Quá ngày deadline cho phép";
        BookCopyEntity bookCopy = borrowedBookCopyEntity.getBookCopy();
        bookCopy.setLostDate(new Date(Calendar.getInstance().getTimeInMillis()));
        bookCopy.setLostReason(lostReasonMessage);
        bookCopyRepo.save(bookCopy);
        borrowedBookCopyRepo.deleteById(borrowedBookCopyEntity.getId());
    }
}

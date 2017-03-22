package jwl.fpt.service.imp;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BookCopyEntity;
import jwl.fpt.entity.BookEntity;
import jwl.fpt.entity.BorrowedBookCopyEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.ReturnCart;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.RfidDto;
import jwl.fpt.repository.BookCopyRepo;
import jwl.fpt.repository.BorrowedBookCopyRepo;
import jwl.fpt.repository.WishBookRepository;
import jwl.fpt.service.IBookReturnService;
import jwl.fpt.util.NotificationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.*;

/**
 * Created by Entaard on 3/17/17.
 */
@Service
public class BookReturnService implements IBookReturnService {
    @Autowired
    private BorrowedBookCopyRepo borrowedBookCopyRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    WishBookRepository wishBookRepository;
    private List<ReturnCart> returnCarts = new ArrayList<>();

    // 0. Check find BorrowedBookCopyEntity by rfid. If not found, return fail: "Book is not being borrowed." Else:
    // 1. Check current cart of the librarian.
    // 2. If no cart found, init a cart and add the book to that cart.
    // 3. If found a cart check user ID:
    ////// a. If userIdOfReturnedBook == userIdInCart, check rfid in return cart, add the book to the cart if not exists.
    ////// b. If userIdOfReturnedBook != userIdInCart, return that book and sth that triggers web's confirmation
    @Override
    public RestServiceModel<BorrowedBookCopyDto> addReturnCopyToCart(RfidDto rfidDto) {
        RestServiceModel<BorrowedBookCopyDto> result = new RestServiceModel<>();
        if (rfidDto == null || rfidDto.getRfid() == null
                || rfidDto.getRfid().isEmpty() || rfidDto.getLibrarianId() == null) {
            result.setFailData(null, "Invalid input librarianId or rfid.");
            return result;
        }

        String rfid = rfidDto.getRfid();
        BookCopyEntity bookCopyEntity = new BookCopyEntity();
        bookCopyEntity.setRfid(rfid);
        BorrowedBookCopyEntity borrowedBookCopyEntity = borrowedBookCopyRepo.findFirst1ByBookCopyAndReturnDateIsNull(bookCopyEntity);
        if (borrowedBookCopyEntity == null) {
            result.setFailData(
                    null,
                    "The book " + rfid + " is not being borrowed.",
                    "Book returned before.");
            return result;
        }

        BorrowedBookCopyDto borrowedBookCopyDto = modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
        String userIdOfReturnedBook = borrowedBookCopyEntity.getAccount().getUserId();
        String librarianId = rfidDto.getLibrarianId();
        ReturnCart returnCart = getCartByLibrarianId(librarianId);
        if (returnCart == null) {
            Set<String> rfids = new HashSet<>();
            rfids.add(rfid);
            returnCart = new ReturnCart(userIdOfReturnedBook, librarianId, rfids);
            returnCarts.add(returnCart);
            result.setSuccessData(
                    borrowedBookCopyDto,
                    "Added returned copy successfully!",
                    "Book added!");
            return result;
        }

        String userIdInCart = returnCart.getUserId();
        if (userIdOfReturnedBook.equals(userIdInCart)) {
            Set<String> rfids = returnCart.getRfids();
            if (rfids == null || rfids.isEmpty()) {
                rfids = new HashSet<>();
                rfids.add(rfid);
                returnCart.setRfids(rfids);
            } else {
                if (rfids.contains(rfid)) {
                    result.setFailData(
                            null,
                            "Book " + rfid + " scanned before!",
                            "Book added before.");
                    return result;
                }
                rfids.add(rfid);
            }
            result.setSuccessData(
                    borrowedBookCopyDto,
                    "Added returned copy successfully!",
                    "Book added!");
            return result;
        }

        result.setWaitForConfirmData(
                borrowedBookCopyDto,
                "New user found. End user " + userIdInCart + " transaction?",
                "New user found. Need confirmation.");
        return result;
    }

    @Override
    @Transactional
    public RestServiceModel<List<BorrowedBookCopyDto>> returnCopies(String librarianId) {
        // TODO: check librarian's role
        RestServiceModel<List<BorrowedBookCopyDto>> result = new RestServiceModel<>();
        if (librarianId == null) {
            result.setFailData(null, "Cannot found librarianId!");
            return result;
        }

        ReturnCart returnCart = getCartByLibrarianId(librarianId);
        if (returnCart == null) {
            result.setFailData(null, "Cannot found return cart. Have you scanned any books?");
            return result;
        }

        result = saveReturnCart(returnCart);
        returnCarts.remove(returnCart);

        return result;
    }

    @Override
    public RestServiceModel cancelReturnCopies(String librarianId) {
        RestServiceModel result = new RestServiceModel();
        if (librarianId == null || librarianId == "") {
            result.setFailData(null, "Librarian Id not found.");
        }
        ReturnCart returnCart = getCartByLibrarianId(librarianId);
        if (returnCart != null) {
            returnCarts.remove(returnCart);
        }

        result.setSuccessData(null, "Cancel successfully");
        return result;
    }

    private ReturnCart getCartByLibrarianId(String librarianId) {
        // TODO: check expire date.
        if (returnCarts == null || returnCarts.isEmpty()) {
            return null;
        }

        for (ReturnCart returnCart :
                returnCarts) {
            if (returnCart.getLibrarianId().equals(librarianId)) {
                return returnCart;
            }
        }

        return null;
    }

    private RestServiceModel<List<BorrowedBookCopyDto>> saveReturnCart(ReturnCart returnCart) {
        RestServiceModel<List<BorrowedBookCopyDto>> result = new RestServiceModel<>();
        Set<String> rfids = returnCart.getRfids();
        //khi nao thi rfids == null?
        if (rfids == null || rfids.isEmpty()) {
            result.setSuccessData(null, "Returned successfully! No book returned. (rfids are null)");
            return result;
        }
        //remove null?
        rfids.remove(null);

        //day la list nhung cuon' sach dang muon ma co' rfid vua quet cua chinh xac 1 *user*
        // xac dinh. truoc do o ham 1.
        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = borrowedBookCopyRepo.findByRfids(rfids);
        if (borrowedBookCopyEntities.isEmpty()) {
            result.setSuccessData(null, "Returned successfully! No book returned.");
            return result;
        }

        List<BorrowedBookCopyDto> borrowedBookCopyDtos = new ArrayList<>();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                borrowedBookCopyEntities) {
            //bug co the xuat hien o day
            BookEntity bookEntity = borrowedBookCopyEntity.getBookCopy().getBook();
            notiWishBook(bookEntity);

            borrowedBookCopyEntity.setReturnDate(new Date(Calendar.getInstance().getTimeInMillis()));
            BorrowedBookCopyDto dto = modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
            borrowedBookCopyDtos.add(dto);
            borrowedBookCopyEntity.getBookCopy().getBook();
        }
        borrowedBookCopyRepo.save(borrowedBookCopyEntities);

        result.setSuccessData(borrowedBookCopyDtos, "Returned book(s) successfully!");
        return result;
    }

    private void notiWishBook(BookEntity bookEntity){
        //day la list nhung ban copies dang muon chua tra cua 1 *tua sach*
        //list nay null or = 0: nghia la khong co ai dang muon sach ma chua tra ca.
        //minh se noti cho user neu
        List<BorrowedBookCopyEntity> totalBrrowedBookCopyEntities =
                borrowedBookCopyRepo.findBorrowingCopiesOfBook(bookEntity.getId());
        //de can than. theo logic toi - thiendn nghi khong the == null
        if (totalBrrowedBookCopyEntities == null) return;
        if  (bookEntity.getNumberOfCopies() - totalBrrowedBookCopyEntities.size() == 0){
            List<AccountEntity> accountEntities = wishBookRepository.findAccountByBookId(bookEntity.getId());
            if (accountEntities == null) return;
            if (accountEntities.size() == 0) return;
            for (AccountEntity accountEntity: accountEntities){
                try {
                    NotificationUtils.pushNotificationWishList(accountEntity.getGoogleToken(), bookEntity.getTitle());
                    wishBookRepository.deleteByBookId(bookEntity.getId());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}

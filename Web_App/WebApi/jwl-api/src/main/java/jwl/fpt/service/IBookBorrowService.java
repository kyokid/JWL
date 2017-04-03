package jwl.fpt.service;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

/**
 * Created by Entaard on 1/29/17.
 */
public interface IBookBorrowService {
    boolean initBorrowSession(HttpServletRequest request, BorrowerDto borrowerDto);
    RfidDtoList addCopiesToSession(HttpServletRequest request, RfidDtoList rfidDtoList);
    List<BorrowedBookCopyDto> checkoutSession(HttpServletRequest request, String userId);

    RestServiceModel<BorrowerDto> initBorrowCart(BorrowerDto borrowerDto, boolean isLibrarian);
    RestServiceModel<RfidDtoList> scanCopiesToCart(RfidDtoList rfidDtoList);
    RestServiceModel<RfidDtoList> scanCopyToCart(RfidDto rfidDto);
    RestServiceModel<List<BorrowedBookCopyDto>> checkoutCart(BorrowerDto borrowerDto, boolean isLibrarian);
    RestServiceModel<Set<String>> cancelAddingCopies(BorrowerDto borrowerDto);
    List<BorrowedBookCopyDto> getBorrowingBookByUserId(AccountDto accountDto);
    List<BorrowedBookCopyDto> deleteBorrowingCopy(BorrowedBookCopyDto borrowedBookCopyDto);
    RestServiceModel<BorrowedBookCopyDto> scanCopyToCartByLibrarian(RfidDto rfidDto);

    RestServiceModel<BorrowedBookCopyDto> renewBorrowedBookCopy(String rfid);

    RestServiceModel<List<BorrowedBookCopyDto>> getHistory(String userId);

    void checkBorrowingBookCopyDeadline() throws UnsupportedEncodingException;
    int calculateUsableBalanceFromDb(String userId);
}

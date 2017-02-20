package jwl.fpt.service;

import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.RfidDtoList;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.BorrowerDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
public interface IBookBorrowService {
    boolean initBorrowSession(HttpServletRequest request, BorrowerDto borrowerDto);
    RfidDtoList addCopiesToSession(HttpServletRequest request, RfidDtoList rfidDtoList);
    List<BorrowedBookCopyDto> checkoutSession(HttpServletRequest request, String userId);

    BorrowerDto initBorrowCart(BorrowerDto borrowerDto);
    RfidDtoList addCopiesToCart(RfidDtoList rfidDtoList);
    List<BorrowedBookCopyDto> checkoutCart(BorrowerDto borrowerDto);
    List<BorrowedBookCopyDto> getBorrowedBookByUserId(AccountDto accountDto);

}

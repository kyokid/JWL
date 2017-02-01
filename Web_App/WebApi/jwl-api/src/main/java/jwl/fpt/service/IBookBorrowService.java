package jwl.fpt.service;

import jwl.fpt.model.dto.BookCopyDtoList;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.BorrowerDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
public interface IBookBorrowService {
    boolean initBorrowSession(HttpServletRequest request, BorrowerDto borrowerDto);
    BookCopyDtoList addCopiesToSession(BookCopyDtoList bookCopyDtoList);
    List<BorrowedBookCopyDto> saveBorrowedCopies();
}

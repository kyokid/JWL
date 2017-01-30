package jwl.fpt.service;

import jwl.fpt.model.dto.BookCopyDto;
import jwl.fpt.model.dto.BorrowedBookCopyDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
public interface IBookBorrowService {
    boolean initBorrowSession(HttpServletRequest request, String userId);
    boolean addCopiesToSession(HttpServletRequest request, List<BookCopyDto> bookCopyDtos);
    List<BorrowedBookCopyDto> saveBorrowedCopies(HttpServletRequest request);
}

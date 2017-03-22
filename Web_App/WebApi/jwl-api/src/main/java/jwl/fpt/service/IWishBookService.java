package jwl.fpt.service;

import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.BookDto;

/**
 * Created by thiendn on 22/03/2017.
 */
public interface IWishBookService {
    BookDto addBookToWishList(String userId, String bookId);
    BookDto removeBookFromWishList(String userId, String bookId);
}

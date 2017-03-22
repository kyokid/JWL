package jwl.fpt.service;

/**
 * Created by thiendn on 22/03/2017.
 */
public interface IWishBookService {
    boolean addBookToWishList(String userId, String bookId);
    boolean removeBookFromWishList(String userId, String bookId);
}

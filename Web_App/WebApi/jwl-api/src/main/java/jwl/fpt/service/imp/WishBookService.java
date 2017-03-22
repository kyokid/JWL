package jwl.fpt.service.imp;

import jwl.fpt.repository.WishBookRepository;
import jwl.fpt.service.IWishBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by thiendn on 22/03/2017.
 */
@Service
public class WishBookService implements IWishBookService{
    @Autowired
    private WishBookRepository wishBookRepository;

    @Override
    public boolean addBookToWishList(String userId, String bookId) {
        int bookIdInt = Integer.parseInt(bookId);
        int result = wishBookRepository.insert(userId, bookIdInt);
        return result > 0;
    }

    @Override
    public boolean removeBookFromWishList(String userId, String bookId) {
        int bookIdInt = Integer.parseInt(bookId);
        int result = wishBookRepository.delete(userId, bookIdInt);
        return result > 0;
    }
}

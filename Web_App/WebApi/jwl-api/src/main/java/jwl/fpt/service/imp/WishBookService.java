package jwl.fpt.service.imp;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.BookEntity;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.BookDto;
import jwl.fpt.repository.AccountRepository;
import jwl.fpt.repository.BookRepo;
import jwl.fpt.repository.WishBookRepository;
import jwl.fpt.service.IWishBookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by thiendn on 22/03/2017.
 */
@Service
public class WishBookService implements IWishBookService{
    @Autowired
    private WishBookRepository wishBookRepository;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public BookDto addBookToWishList(String userId, String bookId) {
        int bookIdInt = Integer.parseInt(bookId);
        int result = wishBookRepository.insert(userId, bookIdInt);
        BookDto bookDto = null;
        if (result > 0){
            bookDto = findBookById(bookIdInt);
            bookDto.setFollow(true);
        }
        return bookDto;
    }

    @Override
    public BookDto removeBookFromWishList(String userId, String bookId) {
        int bookIdInt = Integer.parseInt(bookId);
        int result = wishBookRepository.delete(userId, bookIdInt);
        BookDto bookDto = null;
        if (result > 0){
            bookDto = findBookById(bookIdInt);
            bookDto.setFollow(false);
        }
        return bookDto;
    }

    private BookDto findBookById(int bookId){
        BookEntity bookEntity = bookRepo.findById(bookId);
        BookDto bookDto = modelMapper.map(bookEntity, BookDto.class);
        return bookDto;
    }
}

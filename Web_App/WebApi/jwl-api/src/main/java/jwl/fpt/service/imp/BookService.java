package jwl.fpt.service.imp;

import jwl.fpt.entity.BookEntity;
import jwl.fpt.entity.BookTypeEntity;
import jwl.fpt.entity.BorrowedBookCopyEntity;
import jwl.fpt.entity.WishBookEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BookDetailDto;
import jwl.fpt.model.dto.BookDto;
import jwl.fpt.model.dto.BookTypeDto;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.repository.BookRepo;
import jwl.fpt.repository.BookTypeRepo;
import jwl.fpt.repository.BorrowedBookCopyRepo;
import jwl.fpt.repository.WishBookRepository;
import jwl.fpt.service.IBookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Entaard on 3/19/17.
 */
@Service
public class BookService implements IBookService {
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private BorrowedBookCopyRepo borrowedBookCopyRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private WishBookRepository wishBookRepository;
    @Autowired
    private BookTypeRepo bookTypeRepo;
    @Value("${library.fine.cost}")
    private Integer fineCost;

    @Override
    public RestServiceModel<List<BookDto>> getAllBooks() {
        RestServiceModel<List<BookDto>> result = new RestServiceModel<>();
        List<BookEntity> bookEntities = bookRepo.findAll();

        if (bookEntities == null || bookEntities.isEmpty()) {
            result.setFailData(null, "There is no book in the database.");
            return result;
        }

        List<BookDto> bookDtos = new ArrayList<>();
        for (BookEntity bookEntity :
                bookEntities) {
            BookDto bookDto = modelMapper.map(bookEntity, BookDto.class);
            bookDtos.add(bookDto);
        }
        result.setSuccessData(bookDtos, "Found " + bookDtos.size() + " book(s).");

        return result;
    }

    @Override
    public RestServiceModel<BookDetailDto> getBookDetail(Integer bookId) {
        RestServiceModel<BookDetailDto> result = new RestServiceModel<>();
        if (bookId == null) {
            result.setFailData(null, "Invalid book ID");
            return result;
        }

        BookEntity bookEntity = bookRepo.findById(bookId);
        if (bookEntity == null) {
            result.setFailData(null, "There is no book with ID " + bookId);
            return result;
        }

        BookDetailDto bookDetailDto = modelMapper.map(bookEntity, BookDetailDto.class);
        bookDetailDto.setCautionMoney(calculateCautionMoney(bookEntity));
        result.setSuccessData(bookDetailDto, "Here is the detail of the book " + bookId);

        return result;
    }

    @Override
    public RestServiceModel<List<BorrowedBookCopyDto>> getBorrowingCopies(Integer bookId) {
        RestServiceModel<List<BorrowedBookCopyDto>> result = new RestServiceModel<>();
        if (bookId == null) {
            result.setFailData(null, "Invalid book ID");
            return result;
        }

        List<BorrowedBookCopyEntity> borrowedBookCopyEntities = borrowedBookCopyRepo.findBorrowingCopiesOfBook(bookId);
        if (borrowedBookCopyEntities == null || borrowedBookCopyEntities.isEmpty()) {
            result.setSuccessData(null, "Not found any borrowing copies.");
            return result;
        }

        List<BorrowedBookCopyDto> borrowedBookCopyDtos = new ArrayList<>();
        for (BorrowedBookCopyEntity borrowedBookCopyEntity :
                borrowedBookCopyEntities) {
            BorrowedBookCopyDto borrowedBookCopyDto = modelMapper.map(borrowedBookCopyEntity, BorrowedBookCopyDto.class);
            borrowedBookCopyDtos.add(borrowedBookCopyDto);
        }
        result.setSuccessData(borrowedBookCopyDtos, "Found " + borrowedBookCopyDtos.size() + " borrowing copy(s).");

        return result;
    }

    @Override
    public RestServiceModel<List<BookDto>> searchBooks(String searchTerm, String userId) {
        RestServiceModel<List<BookDto>> result = new RestServiceModel<>();
        List<BookEntity> bookEntities;
        bookEntities = bookRepo.searchBooks(searchTerm);
        List<BookDto> bookDtos = new ArrayList<>();
        for (BookEntity bookEntity: bookEntities){
            BookDto bookDto = modelMapper.map(bookEntity, BookDto.class);
            List<BorrowedBookCopyEntity> borrowedBookCopyEntities =
                    borrowedBookCopyRepo.findBorrowingCopiesOfBook(bookEntity.getId());
            if (borrowedBookCopyEntities == null){
                bookDto.setAvailable(true);
            }else if (borrowedBookCopyEntities.size() == 0){
                bookDto.setAvailable(true);
            }else if(bookEntity.getNumberOfCopies() - borrowedBookCopyEntities.size() > 0) {
                bookDto.setAvailable(true);
            }else if(bookEntity.getNumberOfCopies() == borrowedBookCopyEntities.size()) {
                bookDto.setAvailable(false);
            }
            List<WishBookEntity> wishBookEntitys = wishBookRepository.findByUserIdBookId(userId, bookEntity.getId());
            if (wishBookEntitys == null){
                bookDto.setFollow(false);
            }else if (wishBookEntitys.size() == 0){
                bookDto.setFollow(false);
            }else if (wishBookEntitys.size() > 0){
                bookDto.setFollow(true);
            }
            bookDtos.add(bookDto);
        }
        result.setSuccessData(bookDtos, "Found " + bookDtos.size() + " book(s).");

        return result;
    }

    @Override
    public int calculateCautionMoney(BookEntity bookEntity) {
        BookTypeEntity bookTypeEntity = bookEntity.getBookType();
        int maxLateDate = bookTypeEntity.getLateDaysLimit();
        int bookPrice = bookEntity.getPrice();
        return fineCost * maxLateDate + bookPrice;
    }

    @Override
    public List<BookTypeDto> getBookType() {
        List<BookTypeEntity> bookTypeEntitys = bookTypeRepo.findAll();
        List<BookTypeDto> result = new ArrayList<>();
        for (BookTypeEntity bookTypeEntity: bookTypeEntitys) {
            BookTypeDto bookTypeDto = modelMapper.map(bookTypeEntity, BookTypeDto.class);
            result.add(bookTypeDto);
        }
        return result;
    }
}

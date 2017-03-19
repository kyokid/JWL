package jwl.fpt.service.imp;

import jwl.fpt.entity.BookAuthorEntity;
import jwl.fpt.entity.BookEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BookDto;
import jwl.fpt.repository.BookRepo;
import jwl.fpt.service.IBookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ModelMapper modelMapper;

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

            if (bookEntity.getBookAuthors() != null) {
                List<String> authorNames = new ArrayList<>();
                for (BookAuthorEntity bookAuthorEntity :
                        bookEntity.getBookAuthors()) {
                    String authorName = bookAuthorEntity.getAuthor().getName();
                    authorNames.add(authorName);
                }
                bookDto.setAuthorNames(authorNames);
            }

            bookDtos.add(bookDto);
        }
        result.setSuccessData(bookDtos, "Found " + bookDtos.size() + " book(s).");

        return result;
    }
}

package jwl.fpt.service;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BookDetailDto;
import jwl.fpt.model.dto.BookDto;

import java.util.List;

/**
 * Created by Entaard on 3/19/17.
 */
public interface IBookService {
    RestServiceModel<List<BookDto>> getAllBooks();

    RestServiceModel<BookDetailDto> getBookDetail(Integer bookId);
}

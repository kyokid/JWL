package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BookDto;
import jwl.fpt.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Entaard on 3/19/17.
 */
@RestController
public class BookController {
    @Autowired
    private IBookService bookService;

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public RestServiceModel<List<BookDto>> getAllBooks() {
        return bookService.getAllBooks();
    }
}

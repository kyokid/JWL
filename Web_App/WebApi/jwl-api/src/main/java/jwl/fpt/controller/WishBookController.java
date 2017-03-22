package jwl.fpt.controller;

import jwl.fpt.entity.BookEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BookDto;
import jwl.fpt.service.IWishBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by thiendn on 22/03/2017.
 */
@RestController
public class WishBookController {
    @Autowired
    private IWishBookService wishBookService;
    @RequestMapping(path = "/wishlist/add", method = RequestMethod.GET)
    public RestServiceModel<BookDto> addToWishLish(@RequestParam("user_id") String userId,
                                                   @RequestParam("book_id") String bookId) {
        RestServiceModel<BookDto> restService = new RestServiceModel<>();
        BookDto result = wishBookService.addBookToWishList(userId, bookId);
        restService.setData(result);
        restService.setSucceed(result != null);
        if (result != null){
            restService.setCode("200");
            restService.setTextMessage("Add success! UserId: " + userId + ", BookId: " + bookId);
        }else
        restService.setTextMessage("Add wish list failed!");

        return restService;
    }

    @RequestMapping(path = "/wishlist/remove", method = RequestMethod.GET)
    public RestServiceModel<BookDto> removeFromWishLish(@RequestParam("user_id") String userId,
                                                   @RequestParam("book_id") String bookId) {
        RestServiceModel<BookDto> restService = new RestServiceModel<>();
        BookDto result = wishBookService.removeBookFromWishList(userId, bookId);
        restService.setData(result);
        restService.setSucceed(result != null);
        if (result != null){
            restService.setCode("200");
            restService.setTextMessage("Remove success! UserId: " + userId + ", BookId: " + bookId);
        }else
        restService.setTextMessage("Remove failed!");
        return restService;
    }
}

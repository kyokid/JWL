package jwl.fpt.controller;

import jwl.fpt.entity.AccountEntity;
import jwl.fpt.entity.WishBookEntity;
import jwl.fpt.model.RestServiceModel;
import jwl.fpt.service.IBookService;
import jwl.fpt.service.IUserService;
import jwl.fpt.service.IWishBookService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by thiendn on 22/03/2017.
 */
@RestController
public class WishBookController {
    @Autowired
    private IWishBookService wishBookService;
    @Autowired
    private IBookService bookService;
    @Autowired
    private IUserService userService;

    @RequestMapping(path = "/wishlist/add", method = RequestMethod.GET)
    public RestServiceModel<Boolean> addToWishLish(@RequestParam("user_id") String userId,
                                                   @RequestParam("book_id") String bookId) {
        RestServiceModel<Boolean> restService = new RestServiceModel<>();
        boolean result = wishBookService.addBookToWishList(userId, bookId);
        restService.setData(result);
        restService.setSucceed(result);
        if (result){
            restService.setCode("200");
            restService.setTextMessage("Add success! UserId: " + userId + ", BookId: " + bookId);
        }else
        restService.setTextMessage("Add wish list failed!");

        return restService;
    }

    @RequestMapping(path = "/wishlist/remove", method = RequestMethod.GET)
    public RestServiceModel<Boolean> removeFromWishLish(@RequestParam("user_id") String userId,
                                                   @RequestParam("book_id") String bookId) {
        RestServiceModel<Boolean> restService = new RestServiceModel<>();
        boolean result = wishBookService.removeBookFromWishList(userId, bookId);
        restService.setData(result);
        restService.setSucceed(result);
        if (result){
            restService.setCode("200");
            restService.setTextMessage("Remove success! UserId: " + userId + ", BookId: " + bookId);
        }else
        restService.setTextMessage("Remove failed!");
        return restService;
    }
}

package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.service.IUserService;
import jwl.fpt.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Created by Entaard on 1/29/17.
 */
// TODO: Refactor APIs' name.
@RestController
public class BookBorrowController {
    @Autowired
    private IBookBorrowService bookBorrowService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/init/borrow", method = RequestMethod.POST)
    public RestServiceModel<BorrowerDto> initBorrowCart(@RequestBody BorrowerDto borrowerDto) {
        return bookBorrowService.initBorrowCart(borrowerDto, false);
    }

    @RequestMapping(value = "/add/copies", method = RequestMethod.POST)
    public RestServiceModel<RfidDtoList> addCopiesToCart(@RequestBody RfidDtoList rfidDtoList) {
        return bookBorrowService.addCopiesToCart(rfidDtoList);
    }

    @RequestMapping(value = "/add/copy", method = RequestMethod.POST)
    public RestServiceModel<RfidDtoList> addCopyToCart(@RequestBody RfidDto rfidDto) {
        return bookBorrowService.addCopyToCart(rfidDto);
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public RestServiceModel<List<BorrowedBookCopyDto>> checkoutCart(@RequestBody BorrowerDto borrowerDto) {
        return bookBorrowService.checkoutCart(borrowerDto, false);
    }

    @RequestMapping(value = "/librarian/init/borrow", method = RequestMethod.POST)
    public RestServiceModel<BorrowerDto> initBorrowCartByLibrarian(@RequestBody BorrowerDto borrowerDto) {
        return bookBorrowService.initBorrowCart(borrowerDto, true);
    }

    @RequestMapping(value = "/librarian/add/copy", method = RequestMethod.POST)
    public RestServiceModel<BorrowedBookCopyDto> addCopyToCartByLibrarian(@RequestBody RfidDto rfidDto) {
        RestServiceModel<BorrowedBookCopyDto> responseObj = bookBorrowService.addCopyToCartByLibrarian(rfidDto);
        simpMessagingTemplate.convertAndSend("/socket/add/books", responseObj);

        return responseObj;
    }

    @RequestMapping(value = "/librarian/checkout", method = RequestMethod.POST)
    public RestServiceModel<List<BorrowedBookCopyDto>> checkoutCartByLirarian(@RequestBody BorrowerDto borrowerDto) {
        RestServiceModel<List<BorrowedBookCopyDto>> result =
                bookBorrowService.checkoutCart(borrowerDto, true);
        if (result.isSucceed()){
            String userId = result.getData().get(0).getAccountUserId();
            String token = userService.findByUsername(userId).getGoogleToken();
            NotificationUtils.pushNotiRefreshBorrowedBook(token);
        }
        return result;
    }

    @RequestMapping(value = "/librarian/cancel", method = RequestMethod.POST)
    public RestServiceModel<Set<String>> CancelAddingBooks(@RequestBody BorrowerDto borrowerDto) {
        return bookBorrowService.cancelAddingCopies(borrowerDto);
    }

    @RequestMapping(value = "/getBorrowedBooks", method = RequestMethod.POST)
    public RestServiceModel<List<BorrowedBookCopyDto>> getBorrowingBookByUserId(@RequestBody AccountDto accountDto) {
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = bookBorrowService.getBorrowingBookByUserId(accountDto);
        RestServiceModel<List<BorrowedBookCopyDto>> responseObj = new RestServiceModel<>();
        responseObj.setSucceed(true);
        responseObj.setData(borrowedBookCopyDtos);
        responseObj.setTextMessage("Borrowed of User");
        return responseObj;
    }

    @RequestMapping(value = "/delete/copy", method = RequestMethod.DELETE)
    public RestServiceModel<List<BorrowedBookCopyDto>> deleteBorrowingCopy(@RequestBody BorrowedBookCopyDto borrowedBookCopyDto) {
        // TODO: Add necessary validations.
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = bookBorrowService.deleteBorrowingCopy(borrowedBookCopyDto);
        RestServiceModel<List<BorrowedBookCopyDto>> responseObj = new RestServiceModel<>();
        String[] messages = {"UserId or Copy's id is invalid!", "Copy deleted!"};

        RestServiceModel.checkResult(borrowedBookCopyDtos, responseObj, messages);

        return responseObj;
    }

    @RequestMapping(value = "/renew/{rfid}", method = RequestMethod.GET)
    public RestServiceModel<BorrowedBookCopyDto> renewBookCopy(@PathVariable("rfid") String rfid) {
        return bookBorrowService.renewBorrowedBookCopy(rfid);
    }
}

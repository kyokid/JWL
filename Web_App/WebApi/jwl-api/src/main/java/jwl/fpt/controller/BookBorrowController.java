package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;
import jwl.fpt.service.IBookBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashSet;
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

    @RequestMapping(value = "/init/borrow", method = RequestMethod.POST)
    public RestServiceModel<BorrowerDto> initBorrowCart(@RequestBody BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        BorrowerDto result = bookBorrowService.initBorrowCart(borrowerDto);

        RestServiceModel<BorrowerDto> responseObj = new RestServiceModel<>();
        responseObj.setData(result);
        responseObj.setSucceed(true);
        responseObj.setMessage("Init Checkout");

        return responseObj;
    }

    @RequestMapping(value = "/add/copies", method = RequestMethod.POST)
    public RestServiceModel<RfidDtoList> addCopiesToCart(@RequestBody RfidDtoList rfidDtoList) {
        // TODO: Add necessary validations.
        RfidDtoList result = bookBorrowService.addCopiesToCart(rfidDtoList);
        RestServiceModel<RfidDtoList> responseObj = new RestServiceModel<>();
        String[] messages = {"Invalid user!", "Copies added!"};

        RestServiceModel.checkResult(result, responseObj, messages);

        return responseObj;
    }

    @RequestMapping(value = "/add/copy", method = RequestMethod.POST)
    public RestServiceModel<RfidDtoList> addCopyToCart(@RequestBody RfidDto rfidDto) {
        // TODO: Add necessary validations.
        RfidDtoList rfidDtoList = new RfidDtoList();
        rfidDtoList.setIbeaconId(rfidDto.getIbeaconId());
        Set<String> rfids = new HashSet<>();
        rfids.add(rfidDto.getRfid());
        rfidDtoList.setRfids(rfids);
        RfidDtoList result = bookBorrowService.addCopiesToCart(rfidDtoList);
        RestServiceModel<RfidDtoList> responseObj = new RestServiceModel<>();
        String[] messages = {"Invalid user!", "Copy added!"};

        RestServiceModel.checkResult(result, responseObj, messages);

        return responseObj;
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public RestServiceModel<List<BorrowedBookCopyDto>> checkoutCart(@RequestBody BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = bookBorrowService.checkoutCart(borrowerDto);
        RestServiceModel<List<BorrowedBookCopyDto>> responseObj = new RestServiceModel<>();
        String[] messages = {"Checkout failed!", "User checked out!"};

        RestServiceModel.checkResult(borrowedBookCopyDtos, responseObj, messages);

        return responseObj;
    }

    @RequestMapping(value = "/getBorrowedBooks", method = RequestMethod.POST)
    public RestServiceModel<List<BorrowedBookCopyDto>> getBorrowingBookByUserId(@RequestBody AccountDto accountDto) {
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = bookBorrowService.getBorrowingBookByUserId(accountDto);
        RestServiceModel<List<BorrowedBookCopyDto>> responseObj = new RestServiceModel<>();
        responseObj.setSucceed(true);
        responseObj.setData(borrowedBookCopyDtos);
        responseObj.setMessage("Borrowed of User");
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

    @RequestMapping(value = "/save/copy", method = RequestMethod.POST)
    public RestServiceModel<RfidDtoList> saveCopyToDatabase(@RequestBody RfidDto rfidDto) {
        // TODO: Add necessary validations.
        BorrowedBookCopyDto result = bookBorrowService.saveCopyToDatabase(rfidDto);
        RestServiceModel<RfidDtoList> responseObj = new RestServiceModel<>();
        String[] messages = {"Invalid user!", "Copy saved!"};

        RestServiceModel.checkResult(result, responseObj, messages);
        simpMessagingTemplate.convertAndSend("/socket", responseObj);

        return responseObj;
    }
}

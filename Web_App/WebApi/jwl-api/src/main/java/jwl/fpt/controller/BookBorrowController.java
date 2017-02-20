package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.*;
import jwl.fpt.service.IBookBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Entaard on 1/29/17.
 */
@RestController
public class BookBorrowController {
    @Autowired
    private IBookBorrowService bookBorrowService;

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
    public RestServiceModel<List<BorrowedBookCopyDto>> getBorrowedBookByUserId(@RequestBody AccountDto accountDto) {
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = bookBorrowService.getBorrowedBookByUserId(accountDto);
        RestServiceModel<List<BorrowedBookCopyDto>> responseObj = new RestServiceModel<>();
        responseObj.setSucceed(true);
        responseObj.setData(borrowedBookCopyDtos);
        responseObj.setMessage("Borrowed of User");
        return responseObj;
    }
}

package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.RfidDtoList;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.service.IBookBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
@RestController
public class BookBorrowController {
    @Autowired
    private IBookBorrowService bookBorrowService;

    @RequestMapping(value = "/init/borrow", method = RequestMethod.POST)
    public RestServiceModel<BorrowerDto> initBorrowSession(HttpServletRequest request,
                                                          @RequestBody BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        boolean result = bookBorrowService.initBorrowSession(request, borrowerDto);

        RestServiceModel<BorrowerDto> responseObj = new RestServiceModel<>();
        responseObj.setData(borrowerDto);

        return responseObj;
    }

    @RequestMapping(value = "/add/copies", method = RequestMethod.POST)
    public RestServiceModel<RfidDtoList> addCopiesToSession(HttpServletRequest request,
                                                            @RequestBody RfidDtoList rfidDtoList) {
        // TODO: Add necessary validations.
        RfidDtoList result = bookBorrowService.addCopiesToSession(request, rfidDtoList);
        RestServiceModel<RfidDtoList> responseObj = new RestServiceModel<>();

        if (result == null) {
            responseObj.setSucceed(false);
            responseObj.setMessage("Invalid user!");
        } else {
            responseObj.setData(result);
            responseObj.setSucceed(true);
            responseObj.setMessage("Copies added!");
        }

        return responseObj;
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public RestServiceModel<List<BorrowedBookCopyDto>> checkoutSession(HttpServletRequest request,
                                                                       @RequestBody BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = bookBorrowService.checkoutSession(request, borrowerDto.getUserId());
        RestServiceModel<List<BorrowedBookCopyDto>> responseObj = new RestServiceModel<>();

        if (borrowedBookCopyDtos == null) {
            responseObj.setSucceed(false);
            responseObj.setMessage("Checkout failed!");
        } else {
            responseObj.setData(borrowedBookCopyDtos);
            responseObj.setSucceed(true);
            responseObj.setMessage("User checked out!");
        }

        return responseObj;
    }
}

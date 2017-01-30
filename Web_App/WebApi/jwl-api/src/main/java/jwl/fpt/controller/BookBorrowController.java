package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.BookCopyDto;
import jwl.fpt.model.dto.BookCopyDtoList;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
@RestController
public class BookBorrowController {
    @Autowired
    IBookBorrowService accountService;

    @RequestMapping(value = "/init/borrow", method = RequestMethod.POST)
    public RestServiceModel<BorrowerDto> initBorrowSession(HttpServletRequest request,
                                                          @RequestBody BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        boolean result = accountService.initBorrowSession(request, borrowerDto);
        System.out.print(request.getSession().getAttribute(Constant.SESSION_BORROWER));

        RestServiceModel<BorrowerDto> returnObj = new RestServiceModel<>();
        returnObj.setData(borrowerDto);

        return returnObj;
    }

    @RequestMapping(value = "/add/copies", method = RequestMethod.POST)
    public RestServiceModel<BookCopyDtoList> addCopiesToSession(@RequestBody BookCopyDtoList bookCopyDtoList) {
        // TODO: Add necessary validations.
        BookCopyDtoList result = accountService.addCopiesToSession(bookCopyDtoList);
        RestServiceModel<BookCopyDtoList> returnObj = new RestServiceModel<>();

        if (result == null) {
            returnObj.setSucceed(false);
            returnObj.setMessage("Invalid user!");
        }

        System.out.print(result.toString());

        returnObj.setData(result);

        return returnObj;
    }
}

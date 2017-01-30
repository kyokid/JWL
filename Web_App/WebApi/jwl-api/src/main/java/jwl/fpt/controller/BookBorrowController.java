package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.AccountDto;
import jwl.fpt.model.dto.BookCopyDto;
import jwl.fpt.service.IBookBorrowService;
import jwl.fpt.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/borrower/init/borrow", method = RequestMethod.POST)
    public RestServiceModel<AccountDto> initBorrowSession(HttpServletRequest request,
                                                          @RequestBody AccountDto accountDto) {
        // TODO: Add necessary validations.
        String userId = accountDto.getUserId();
        boolean result = accountService.initBorrowSession(request, userId);
        System.out.print(request.getSession().getAttribute(Constant.SESSION_BORROWER));

        RestServiceModel<AccountDto> returnObj = new RestServiceModel<>();
        returnObj.setData(accountDto);

        return returnObj;
    }

    @RequestMapping(value = "/borrower/add/copies", method = RequestMethod.POST)
    public RestServiceModel<List<BookCopyDto>> addCopiesToSession(HttpServletRequest request,
                                                           @RequestBody List<BookCopyDto> bookCopyDtos) {
        // TODO: Add necessary validations.
        boolean result = accountService.addCopiesToSession(request, bookCopyDtos);

        HttpSession session = request.getSession(false);
        List<BookCopyDto> bookCopies = (List<BookCopyDto>) session.getAttribute(Constant.SESSION_PENDING_COPIES);
        System.out.print(bookCopies.toString());

        RestServiceModel<List<BookCopyDto>> returnObj = new RestServiceModel<>();
        returnObj.setData(bookCopyDtos);

        return returnObj;
    }
}

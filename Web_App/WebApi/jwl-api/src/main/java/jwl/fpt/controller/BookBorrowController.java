package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.BorrowerDto;
import jwl.fpt.model.dto.RfidDto;
import jwl.fpt.model.dto.RfidDtoList;
import jwl.fpt.service.IBookBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
        responseObj.setSucceed(true);
        responseObj.setMessage("Init Checkout");

        return responseObj;
    }

    @RequestMapping(value = "/add/copies", method = RequestMethod.POST)
    public RestServiceModel<RfidDtoList> addCopiesToSession(HttpServletRequest request,
                                                            @RequestBody RfidDtoList rfidDtoList) {
        // TODO: Add necessary validations.
        RfidDtoList result = bookBorrowService.addCopiesToSession(request, rfidDtoList);
        RestServiceModel<RfidDtoList> responseObj = new RestServiceModel<>();
        String[] messages = {"Invalid user!", "Copies added!"};

        RestServiceModel.checkResult(result, responseObj, messages);

        return responseObj;
    }

    /**
     * Add a single copy to user's session.
     * @param request user request.
     * @param rfidDto dto that holds the ibeaconId and the rfid of a book copy.
     * @return RestServiceModel
     */
    @RequestMapping(value = "/add/copy", method = RequestMethod.POST)
    public RestServiceModel<RfidDtoList> addCopyToSession(HttpServletRequest request,
                                                            @RequestBody RfidDto rfidDto) {
        // TODO: Add necessary validations.
        RfidDtoList rfidDtoList = new RfidDtoList();
        rfidDtoList.setIbeaconId(rfidDto.getIbeaconId());
        List<String> rfids = new ArrayList<>();
        rfids.add(rfidDto.getRfid());
        rfidDtoList.setRfids(rfids);
        RfidDtoList result = bookBorrowService.addCopiesToSession(request, rfidDtoList);
        RestServiceModel<RfidDtoList> responseObj = new RestServiceModel<>();
        String[] messages = {"Invalid user!", "Copy added!"};

        RestServiceModel.checkResult(result, responseObj, messages);

        return responseObj;
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public RestServiceModel<List<BorrowedBookCopyDto>> checkoutSession(HttpServletRequest request,
                                                                       @RequestBody BorrowerDto borrowerDto) {
        // TODO: Add necessary validations.
        List<BorrowedBookCopyDto> borrowedBookCopyDtos = bookBorrowService.checkoutSession(request, borrowerDto.getUserId());
        RestServiceModel<List<BorrowedBookCopyDto>> responseObj = new RestServiceModel<>();
        String[] messages = {"Checkout failed!", "User checked out!"};

        RestServiceModel.checkResult(borrowedBookCopyDtos, responseObj, messages);

        return responseObj;
    }
}

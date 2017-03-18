package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.RfidDto;
import jwl.fpt.service.IBookReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Entaard on 3/17/17.
 */
@RestController
public class BookReturnController {
    @Autowired
    IBookReturnService bookReturnService;

    @RequestMapping(value = "librarian/add/return", method = RequestMethod.POST)
    public RestServiceModel<BorrowedBookCopyDto> addReturnCopyToCart(@RequestBody RfidDto rfidDto) {
        return bookReturnService.addReturnCopyToCart(rfidDto);
    }

    @RequestMapping(value = "librarian/{librarianId}/commit/return", method = RequestMethod.GET)
    public RestServiceModel<List<BorrowedBookCopyDto>> commitReturnCopies(@PathVariable("librarianId") String librarianId) {
        return bookReturnService.returnCopies(librarianId);
    }

    @RequestMapping(value = "librarian/{librarianId}/cancel/return", method = RequestMethod.GET)
    public RestServiceModel cancelreturnCopies(@PathVariable("librarianId") String librarianId) {
        return bookReturnService.cancelReturnCopies(librarianId);
    }
}

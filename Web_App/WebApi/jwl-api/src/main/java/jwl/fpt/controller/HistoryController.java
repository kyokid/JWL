package jwl.fpt.controller;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.service.IBookBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * Created by thiendn on 31/03/2017.
 */
@RestController
public class HistoryController {
    @Autowired
    private IBookBorrowService bookBorrowService;

    @RequestMapping(path = "/history/borrowed_books/{userId}", method = RequestMethod.GET)
    RestServiceModel<List<BorrowedBookCopyDto>> getHistory(@PathVariable("userId") String userId){
        return bookBorrowService.getHistory(userId);
    }
}

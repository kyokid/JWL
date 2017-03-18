package jwl.fpt.service;

import jwl.fpt.model.RestServiceModel;
import jwl.fpt.model.dto.BorrowedBookCopyDto;
import jwl.fpt.model.dto.RfidDto;

import java.util.List;

/**
 * Created by Entaard on 3/17/17.
 */
public interface IBookReturnService {
    RestServiceModel<BorrowedBookCopyDto> addReturnCopyToCart(RfidDto rfidDto);
    RestServiceModel<List<BorrowedBookCopyDto>> returnCopies(String librarianId);
    RestServiceModel cancelReturnCopies(String librarianId);
}

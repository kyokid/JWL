package jwl.fpt.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
@Data
public class BookCopyDto {
    private String rfid;
    private int bookId;
    private BorrowedBookCopyDto borrowingCopyInfo;
}

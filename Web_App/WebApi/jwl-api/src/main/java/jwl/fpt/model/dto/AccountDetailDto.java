package jwl.fpt.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Entaard on 2/21/17.
 */
@Data
public class AccountDetailDto {
    private String userId;
    private Boolean inLibrary;
    private Boolean activated;
    private ProfileDto profile;
    private List<BorrowedBookCopyDto> borrowedBookCopies;
}

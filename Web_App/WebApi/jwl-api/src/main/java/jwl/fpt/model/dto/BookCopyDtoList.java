package jwl.fpt.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Entaard on 1/30/17.
 */
@Data
public class BookCopyDtoList {
    private String ibeaconId;
    private List<BookCopyDto> bookCopyDtos;
}

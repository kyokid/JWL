package jwl.fpt.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Entaard on 3/19/17.
 */
@Data
public class BookDto {
    private Integer id;
    private String title;
    private String publisher;
    private List<BookAuthorDto> bookAuthors;
    private Integer publishYear;
    private Integer numberOfCopies;
    private String isbn;
    private boolean isAvailable;
    private boolean isFollow;
}

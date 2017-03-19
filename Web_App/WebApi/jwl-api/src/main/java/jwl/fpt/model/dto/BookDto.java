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
    private List<String> authorNames;
    private Integer publishYear;
    private Integer numberOfCopies;
    private String isbn;
}

package jwl.fpt.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Entaard on 3/19/17.
 */
@Data
public class BookDetailDto {
    private Integer id;
    private String title;
    private String publisher;
    private String description;
    private Integer publishYear;
    private Integer numberOfPages;
    private Integer numberOfCopies;
    private String isbn;
    private int price;
    private String thumbnail;
    private BookTypeDto bookType;
    private BookPositionDto bookPosition;
    private List<BookAuthorDto> bookAuthors;
    private List<BookCategoryDto> bookCategories;
}

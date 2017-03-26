package jwl.fpt.model.dto;

import lombok.Data;

import java.sql.Date;
import java.util.List;

/**
 * Created by Entaard on 1/29/17.
 */
@Data
public class BorrowedBookCopyDto {
    private Integer id;
    private String bookCopyRfid;
    private String accountUserId;
    private Integer borrowLimitDays;
    private Integer extendTimes;
    private Integer extendTimesLimit;
    private Integer daysPerExtend;
    private Date borrowedDate;
    private Date returnDate;
    private Date deadlineDate;
    private Integer extendNumber;
    private Integer rootId;
    private String bookCopyBookId;
    private String bookCopyBookTitle;
    private String bookCopyBookPublisher;
    private String bookCopyBookDescription;
    private Integer bookCopyBookNumberOfPages;
    private Integer bookCopyBookPublishYear;
    private Integer bookCopyBookPrice;
    private String bookCopyBookThumbnail;
    private List<BookAuthorDto> bookCopyBookBookAuthors;
    private List<BookCategoryDto> bookCopyBookBookCategories;
}

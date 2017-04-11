package jwl.fpt.model.dto;

import jwl.fpt.util.Helper;
import lombok.Data;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import static jwl.fpt.util.Constant.BOOK_STATUS_OK;

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
    private Integer bookCopyBookBookTypeDaysPerExtend;
    private Integer bookCopyBookBookTypeLateDaysLimit;
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
    private Integer bookStatus;
    private Integer cautionMoney;
    private String bookCopyBookBookTypeName;
    private List<BookAuthorDto> bookCopyBookBookAuthors;
    private List<BookCategoryDto> bookCopyBookBookCategories;

    public static void setBookStatusForOneDto(BorrowedBookCopyDto borrowedBookCopyDto) {
        if (borrowedBookCopyDto == null || borrowedBookCopyDto.getDeadlineDate() == null) {
            return;
        }
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        Date deadline = borrowedBookCopyDto.getDeadlineDate();
        int daysInterval = Helper.getDaysInterval(deadline, currentDate);
        borrowedBookCopyDto.setBookStatus(daysInterval);
    }

    public static void setBookStatusForListDtos(List<BorrowedBookCopyDto> borrowedBookCopyDtos) {
        if (borrowedBookCopyDtos == null || borrowedBookCopyDtos.isEmpty()) {
            return;
        }

        for (BorrowedBookCopyDto borrowedBookCopyDto :
                borrowedBookCopyDtos) {
            setBookStatusForOneDto(borrowedBookCopyDto);
        }
    }
}

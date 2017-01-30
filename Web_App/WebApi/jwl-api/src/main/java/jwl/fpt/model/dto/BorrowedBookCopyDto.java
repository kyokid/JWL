package jwl.fpt.model.dto;

import lombok.Data;

import java.sql.Date;

/**
 * Created by Entaard on 1/29/17.
 */
@Data
public class BorrowedBookCopyDto {
    private int id;
    private String bookCopyId;
    private String userId;
    private int borrowLimitDays;
    private int extendTimes;
    private int extendTimesLimit;
    private int daysPerExtend;
    private Date borrowedDate;
    private Date returnDate;
}

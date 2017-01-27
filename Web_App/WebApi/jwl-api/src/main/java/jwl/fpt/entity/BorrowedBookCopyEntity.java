package jwl.fpt.entity;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by Entaard on 1/27/17.
 */
@Entity
@Table(name = "borrowed_book_copy", schema = "public", catalog = "jwl_test")
public class BorrowedBookCopyEntity {
    private int id;
    private String bookCopyId;
    private String userId;
    private int borrowLimitDays;
    private int extendTimes;
    private int extendTimesLimit;
    private int daysPerExtend;
    private Date borrowedDate;
    private Date returnDate;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "book_copy_id")
    public String getBookCopyId() {
        return bookCopyId;
    }

    public void setBookCopyId(String bookCopyId) {
        this.bookCopyId = bookCopyId;
    }

    @Basic
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "borrow_limit_days")
    public int getBorrowLimitDays() {
        return borrowLimitDays;
    }

    public void setBorrowLimitDays(int borrowLimitDays) {
        this.borrowLimitDays = borrowLimitDays;
    }

    @Basic
    @Column(name = "extend_times")
    public int getExtendTimes() {
        return extendTimes;
    }

    public void setExtendTimes(int extendTimes) {
        this.extendTimes = extendTimes;
    }

    @Basic
    @Column(name = "extend_times_limit")
    public int getExtendTimesLimit() {
        return extendTimesLimit;
    }

    public void setExtendTimesLimit(int extendTimesLimit) {
        this.extendTimesLimit = extendTimesLimit;
    }

    @Basic
    @Column(name = "days_per_extend")
    public int getDaysPerExtend() {
        return daysPerExtend;
    }

    public void setDaysPerExtend(int daysPerExtend) {
        this.daysPerExtend = daysPerExtend;
    }

    @Basic
    @Column(name = "borrowed_date")
    public Date getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(Date borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    @Basic
    @Column(name = "return_date")
    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BorrowedBookCopyEntity that = (BorrowedBookCopyEntity) o;

        if (id != that.id) return false;
        if (borrowLimitDays != that.borrowLimitDays) return false;
        if (extendTimes != that.extendTimes) return false;
        if (extendTimesLimit != that.extendTimesLimit) return false;
        if (daysPerExtend != that.daysPerExtend) return false;
        if (bookCopyId != null ? !bookCopyId.equals(that.bookCopyId) : that.bookCopyId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (borrowedDate != null ? !borrowedDate.equals(that.borrowedDate) : that.borrowedDate != null) return false;
        if (returnDate != null ? !returnDate.equals(that.returnDate) : that.returnDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (bookCopyId != null ? bookCopyId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + borrowLimitDays;
        result = 31 * result + extendTimes;
        result = 31 * result + extendTimesLimit;
        result = 31 * result + daysPerExtend;
        result = 31 * result + (borrowedDate != null ? borrowedDate.hashCode() : 0);
        result = 31 * result + (returnDate != null ? returnDate.hashCode() : 0);
        return result;
    }
}

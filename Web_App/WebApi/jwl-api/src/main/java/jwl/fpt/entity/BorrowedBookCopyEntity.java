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
    private Date borrowedDate;
    private Date returnDate;
    private Date deadlineDate;
    private int extendNumber;
    private Integer rootId;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="SEQ_GEN", sequenceName="borrowed_book_copy_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
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

    @Basic
    @Column(name = "deadline_date")
    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    @Basic
    @Column(name = "extend_number")
    public int getExtendNumber() {
        return extendNumber;
    }

    public void setExtendNumber(int extendNumber) {
        this.extendNumber = extendNumber;
    }

    @Basic
    @Column(name = "root_id")
    public Integer getRootId() {
        return rootId;
    }

    public void setRootId(Integer rootId) {
        this.rootId = rootId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BorrowedBookCopyEntity that = (BorrowedBookCopyEntity) o;

        if (id != that.id) return false;
        if (extendNumber != that.extendNumber) return false;
        if (borrowedDate != null ? !borrowedDate.equals(that.borrowedDate) : that.borrowedDate != null) return false;
        if (returnDate != null ? !returnDate.equals(that.returnDate) : that.returnDate != null) return false;
        if (deadlineDate != null ? !deadlineDate.equals(that.deadlineDate) : that.deadlineDate != null) return false;
        if (rootId != null ? !rootId.equals(that.rootId) : that.rootId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (borrowedDate != null ? borrowedDate.hashCode() : 0);
        result = 31 * result + (returnDate != null ? returnDate.hashCode() : 0);
        result = 31 * result + (deadlineDate != null ? deadlineDate.hashCode() : 0);
        result = 31 * result + extendNumber;
        result = 31 * result + (rootId != null ? rootId.hashCode() : 0);
        return result;
    }
}

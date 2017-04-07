package jwl.fpt.entity;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "borrowed_book_copy", schema = "public", catalog = "jwl_test")
public class BorrowedBookCopyEntity {
    private Integer id;
    private Date borrowedDate;
    private Date returnDate;
    private Date deadlineDate;
    private Integer extendNumber;
    private Integer rootId;
    private Integer cautionMoney;
    private Integer notiStatus;
    private BookCopyEntity bookCopy;
    private AccountEntity account;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="SEQ_GEN", sequenceName="borrowed_book_copy_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public Integer getExtendNumber() {
        return extendNumber;
    }

    public void setExtendNumber(Integer extendNumber) {
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

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (borrowedDate != null ? !borrowedDate.equals(that.borrowedDate) : that.borrowedDate != null) return false;
        if (returnDate != null ? !returnDate.equals(that.returnDate) : that.returnDate != null) return false;
        if (deadlineDate != null ? !deadlineDate.equals(that.deadlineDate) : that.deadlineDate != null) return false;
        if (extendNumber != null ? !extendNumber.equals(that.extendNumber) : that.extendNumber != null) return false;
        if (rootId != null ? !rootId.equals(that.rootId) : that.rootId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (borrowedDate != null ? borrowedDate.hashCode() : 0);
        result = 31 * result + (returnDate != null ? returnDate.hashCode() : 0);
        result = 31 * result + (deadlineDate != null ? deadlineDate.hashCode() : 0);
        result = 31 * result + (extendNumber != null ? extendNumber.hashCode() : 0);
        result = 31 * result + (rootId != null ? rootId.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "book_copy_id", referencedColumnName = "rfid", nullable = false)
    public BookCopyEntity getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopyEntity bookCopyByBookCopyId) {
        this.bookCopy = bookCopyByBookCopyId;
    }

    public void setBookCopy(String rfid) {
        BookCopyEntity bookCopyEntity = new BookCopyEntity();
        bookCopyEntity.setRfid(rfid);
        this.bookCopy = bookCopyEntity;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity accountByUserId) {
        this.account = accountByUserId;
    }

    public void setAccount(String userId) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUserId(userId);
        this.account = accountEntity;
    }

    @Basic
    @Column(name = "caution_money")
    public Integer getCautionMoney() {
        return cautionMoney;
    }

    public void setCautionMoney(Integer cautionMoney) {
        this.cautionMoney = cautionMoney;
    }

    @Basic
    @Column(name = "noti_status")
    public Integer getNotiStatus() {
        return notiStatus;
    }

    public void setNotiStatus(Integer notiStatus) {
        this.notiStatus = notiStatus;
    }
}

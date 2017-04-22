package jwl.fpt.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "book_copy", schema = "public", catalog = "jwl_test")
public class BookCopyEntity {
    private String rfid;
    private BookEntity book;
    private Date lostDate;
    private String lostReason;
    private Collection<BorrowedBookCopyEntity> borrowedBookCopies;

    @Id
    @Column(name = "rfid")
    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookCopyEntity that = (BookCopyEntity) o;

        if (rfid != null ? !rfid.equals(that.rfid) : that.rfid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rfid != null ? rfid.hashCode() : 0;
        result = 31 * result;
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    public BookEntity getBook() {
        return book;
    }

    public void setBook(BookEntity bookByBookId) {
        this.book = bookByBookId;
    }

    public void setBook(Integer bookId) {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(bookId);
        this.book = bookEntity;
    }

    @OneToMany(mappedBy = "bookCopy")
    public Collection<BorrowedBookCopyEntity> getBorrowedBookCopies() {
        return borrowedBookCopies;
    }

    public void setBorrowedBookCopies(Collection<BorrowedBookCopyEntity> borrowedBookCopiesByRfid) {
        this.borrowedBookCopies = borrowedBookCopiesByRfid;
    }



    @Basic
    @Column(name = "lost_date")
    public Date getLostDate() {
        return lostDate;
    }

    @Basic
    @Column(name = "lost_date")
    public void setLostDate(Date lostDate) {
        this.lostDate = lostDate;
    }

    @Basic
    @Column(name = "lost_reason")
    public String getLostReason() {
        return lostReason;
    }

    @Basic
    @Column(name = "lost_reason")
    public void setLostReason(String lostReason) {
        this.lostReason = lostReason;
    }
}

package jwl.fpt.entity;

import javax.persistence.*;

/**
 * Created by Entaard on 1/27/17.
 */
@Entity
@Table(name = "book_copy", schema = "public", catalog = "jwl_test")
public class BookCopyEntity {
    private String rfid;
    private int bookId;
    private int price;

    @Id
    @Column(name = "rfid")
    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    @Basic
    @Column(name = "book_id")
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    @Basic
    @Column(name = "price")
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookCopyEntity that = (BookCopyEntity) o;

        if (bookId != that.bookId) return false;
        if (price != that.price) return false;
        if (rfid != null ? !rfid.equals(that.rfid) : that.rfid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rfid != null ? rfid.hashCode() : 0;
        result = 31 * result + bookId;
        result = 31 * result + price;
        return result;
    }
}

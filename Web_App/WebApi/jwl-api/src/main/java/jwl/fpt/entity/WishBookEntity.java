package jwl.fpt.entity;

import javax.persistence.*;

/**
 * Created by Entaard on 1/27/17.
 */
@Entity
@Table(name = "wish_book", schema = "public", catalog = "jwl_test")
@IdClass(WishBookEntityPK.class)
public class WishBookEntity {
    private int bookId;
    private String userId;

    @Id
    @Column(name = "book_id")
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    @Id
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WishBookEntity that = (WishBookEntity) o;

        if (bookId != that.bookId) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bookId;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}

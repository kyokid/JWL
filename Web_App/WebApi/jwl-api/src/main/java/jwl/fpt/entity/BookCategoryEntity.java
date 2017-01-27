package jwl.fpt.entity;

import javax.persistence.*;

/**
 * Created by Entaard on 1/27/17.
 */
@Entity
@Table(name = "book_category", schema = "public", catalog = "jwl_test")
@IdClass(BookCategoryEntityPK.class)
public class BookCategoryEntity {
    private int bookId;
    private int categoryId;

    @Id
    @Column(name = "book_id")
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    @Id
    @Column(name = "category_id")
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookCategoryEntity that = (BookCategoryEntity) o;

        if (bookId != that.bookId) return false;
        if (categoryId != that.categoryId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bookId;
        result = 31 * result + categoryId;
        return result;
    }
}

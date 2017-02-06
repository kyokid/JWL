package jwl.fpt.entity;

import javax.persistence.*;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "book_category", schema = "public", catalog = "jwl_test")
public class BookCategoryEntity {
    private Integer id;
    private BookEntity book;
    private CategoryEntity category;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="SEQ_GEN", sequenceName="book_category_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookCategoryEntity that = (BookCategoryEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
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

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity categoryByCategoryId) {
        this.category = categoryByCategoryId;
    }

    public void setCategory(Integer categoryId) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryId);
        this.category = categoryEntity;
    }
}

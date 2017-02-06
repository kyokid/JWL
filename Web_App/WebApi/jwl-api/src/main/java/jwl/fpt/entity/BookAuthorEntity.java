package jwl.fpt.entity;

import javax.persistence.*;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "book_author", schema = "public", catalog = "jwl_test")
public class BookAuthorEntity {
    private Integer id;
    private BookEntity book;
    private AuthorEntity author;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="SEQ_GEN", sequenceName="book_author_id_seq")
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

        BookAuthorEntity that = (BookAuthorEntity) o;

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
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity authorByAuthorId) {
        this.author = authorByAuthorId;
    }

    public void setAuthor(Integer authorId) {
        AuthorEntity authorEntity = new AuthorEntity();
        authorEntity.setId(authorId);
        this.author = authorEntity;
    }
}

package jwl.fpt.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "book", schema = "public", catalog = "jwl_test")
public class BookEntity {
    private Integer id;
    private String title;
    private String publisher;
    private String description;
    private Integer publishYear;
    private Integer numberOfPages;
    private Integer numberOfCopies;
    private String isbn;
    private Integer price;
    private String thumbnail;
    private Date deleteDate;
    private BookTypeEntity bookType;
    private BookPositionEntity bookPosition;
    private Collection<BookAuthorEntity> bookAuthors;
    private Collection<BookCategoryEntity> bookCategories;
    private Collection<BookCopyEntity> bookCopies;
    private Collection<WishBookEntity> wishBooks;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="SEQ_GEN", sequenceName="book_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "publisher")
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "publish_year")
    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    @Basic
    @Column(name = "number_of_pages")
    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    @Basic
    @Column(name = "number_of_copies")
    public Integer getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(Integer numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    @Basic
    @Column(name = "thumbnail")
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Basic
    @Column(name = "price")
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Basic
    @Column(name = "isbn")
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Basic
    @Column(name = "delete_date")
    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookEntity that = (BookEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (publisher != null ? !publisher.equals(that.publisher) : that.publisher != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (publishYear != null ? !publishYear.equals(that.publishYear) : that.publishYear != null) return false;
        if (numberOfPages != null ? !numberOfPages.equals(that.numberOfPages) : that.numberOfPages != null)
            return false;
        if (numberOfCopies != null ? !numberOfCopies.equals(that.numberOfCopies) : that.numberOfCopies != null)
            return false;
        if (isbn != null ? !isbn.equals(that.isbn) : that.isbn != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (publisher != null ? publisher.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (publishYear != null ? publishYear.hashCode() : 0);
        result = 31 * result + (numberOfPages != null ? numberOfPages.hashCode() : 0);
        result = 31 * result + (numberOfCopies != null ? numberOfCopies.hashCode() : 0);
        result = 31 * result + (isbn != null ? isbn.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "book_type_id", referencedColumnName = "id", nullable = false)
    public BookTypeEntity getBookType() {
        return bookType;
    }

    public void setBookType(BookTypeEntity bookTypeByBookTypeId) {
        this.bookType = bookTypeByBookTypeId;
    }

    public void setBookType(Integer bookTypeId) {
        BookTypeEntity bookTypeEntity = new BookTypeEntity();
        bookTypeEntity.setId(bookTypeId);
        this.bookType = bookTypeEntity;
    }

    @ManyToOne
    @JoinColumn(name = "position_id", referencedColumnName = "id")
    public BookPositionEntity getBookPosition() {
        return bookPosition;
    }

    public void setBookPosition(BookPositionEntity bookPositionByPositionId) {
        this.bookPosition = bookPositionByPositionId;
    }

    public void setBookPosition(Integer positionId) {
        BookPositionEntity bookPositionEntity = new BookPositionEntity();
        bookPositionEntity.setId(positionId);
        this.bookPosition = bookPositionEntity;
    }

    @OneToMany(mappedBy = "book")
    public Collection<BookAuthorEntity> getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(Collection<BookAuthorEntity> bookAuthorsById) {
        this.bookAuthors = bookAuthorsById;
    }

    @OneToMany(mappedBy = "book")
    public Collection<BookCategoryEntity> getBookCategories() {
        return bookCategories;
    }

    public void setBookCategories(Collection<BookCategoryEntity> bookCategoriesById) {
        this.bookCategories = bookCategoriesById;
    }

    @OneToMany(mappedBy = "book")
    public Collection<BookCopyEntity> getBookCopies() {
        return bookCopies;
    }

    public void setBookCopies(Collection<BookCopyEntity> bookCopiesById) {
        this.bookCopies = bookCopiesById;
    }

    @OneToMany(mappedBy = "book")
    public Collection<WishBookEntity> getWishBooks() {
        return wishBooks;
    }

    public void setWishBooks(Collection<WishBookEntity> wishBooksById) {
        this.wishBooks = wishBooksById;
    }
}

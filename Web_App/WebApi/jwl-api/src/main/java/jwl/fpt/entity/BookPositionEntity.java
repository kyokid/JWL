package jwl.fpt.entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "book_position", schema = "public", catalog = "jwl_test")
public class BookPositionEntity {
    private Integer id;
    private String shelf;
    private String floor;
    private Collection<BookEntity> books;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="SEQ_GEN", sequenceName="book_position_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "shelf")
    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    @Basic
    @Column(name = "floor")
    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookPositionEntity that = (BookPositionEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (shelf != null ? !shelf.equals(that.shelf) : that.shelf != null) return false;
        if (floor != null ? !floor.equals(that.floor) : that.floor != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (shelf != null ? shelf.hashCode() : 0);
        result = 31 * result + (floor != null ? floor.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "bookPosition")
    public Collection<BookEntity> getBooks() {
        return books;
    }

    public void setBooks(Collection<BookEntity> booksById) {
        this.books = booksById;
    }
}

package jwl.fpt.entity;

import javax.persistence.*;

/**
 * Created by Entaard on 1/27/17.
 */
@Entity
@Table(name = "book_position", schema = "public", catalog = "jwl_test")
public class BookPositionEntity {
    private int id;
    private String shelf;
    private String floor;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

        if (id != that.id) return false;
        if (shelf != null ? !shelf.equals(that.shelf) : that.shelf != null) return false;
        if (floor != null ? !floor.equals(that.floor) : that.floor != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (shelf != null ? shelf.hashCode() : 0);
        result = 31 * result + (floor != null ? floor.hashCode() : 0);
        return result;
    }
}

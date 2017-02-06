package jwl.fpt.entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "author", schema = "public", catalog = "jwl_test")
public class AuthorEntity {
    private Integer id;
    private String name;
    private String description;
    private Collection<BookAuthorEntity> bookAuthors;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="SEQ_GEN", sequenceName="author_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorEntity that = (AuthorEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "author")
    public Collection<BookAuthorEntity> getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(Collection<BookAuthorEntity> bookAuthorsById) {
        this.bookAuthors = bookAuthorsById;
    }
}

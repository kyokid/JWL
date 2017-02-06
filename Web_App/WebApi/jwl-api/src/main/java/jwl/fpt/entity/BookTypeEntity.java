package jwl.fpt.entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "book_type", schema = "public", catalog = "jwl_test")
public class BookTypeEntity {
    private Integer id;
    private String name;
    private Integer borrowLimitDays;
    private Integer daysPerExtend;
    private Integer extendTimesLimit;
    private Collection<BookEntity> books;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="SEQ_GEN", sequenceName="book_type_id_seq")
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
    @Column(name = "borrow_limit_days")
    public Integer getBorrowLimitDays() {
        return borrowLimitDays;
    }

    public void setBorrowLimitDays(Integer borrowLimitDays) {
        this.borrowLimitDays = borrowLimitDays;
    }

    @Basic
    @Column(name = "days_per_extend")
    public Integer getDaysPerExtend() {
        return daysPerExtend;
    }

    public void setDaysPerExtend(Integer daysPerExtend) {
        this.daysPerExtend = daysPerExtend;
    }

    @Basic
    @Column(name = "extend_times_limit")
    public Integer getExtendTimesLimit() {
        return extendTimesLimit;
    }

    public void setExtendTimesLimit(Integer extendTimesLimit) {
        this.extendTimesLimit = extendTimesLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookTypeEntity that = (BookTypeEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (borrowLimitDays != null ? !borrowLimitDays.equals(that.borrowLimitDays) : that.borrowLimitDays != null)
            return false;
        if (daysPerExtend != null ? !daysPerExtend.equals(that.daysPerExtend) : that.daysPerExtend != null)
            return false;
        if (extendTimesLimit != null ? !extendTimesLimit.equals(that.extendTimesLimit) : that.extendTimesLimit != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (borrowLimitDays != null ? borrowLimitDays.hashCode() : 0);
        result = 31 * result + (daysPerExtend != null ? daysPerExtend.hashCode() : 0);
        result = 31 * result + (extendTimesLimit != null ? extendTimesLimit.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "bookType")
    public Collection<BookEntity> getBooks() {
        return books;
    }

    public void setBooks(Collection<BookEntity> booksById) {
        this.books = booksById;
    }
}

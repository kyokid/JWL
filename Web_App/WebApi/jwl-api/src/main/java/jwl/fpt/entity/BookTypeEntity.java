package jwl.fpt.entity;

import javax.persistence.*;

/**
 * Created by Entaard on 1/27/17.
 */
@Entity
@Table(name = "book_type", schema = "public", catalog = "jwl_test")
public class BookTypeEntity {
    private int id;
    private String name;
    private int borrowLimitDays;
    private int daysPerExtend;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    public int getBorrowLimitDays() {
        return borrowLimitDays;
    }

    public void setBorrowLimitDays(int borrowLimitDays) {
        this.borrowLimitDays = borrowLimitDays;
    }

    @Basic
    @Column(name = "days_per_extend")
    public int getDaysPerExtend() {
        return daysPerExtend;
    }

    public void setDaysPerExtend(int daysPerExtend) {
        this.daysPerExtend = daysPerExtend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookTypeEntity that = (BookTypeEntity) o;

        if (id != that.id) return false;
        if (borrowLimitDays != that.borrowLimitDays) return false;
        if (daysPerExtend != that.daysPerExtend) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + borrowLimitDays;
        result = 31 * result + daysPerExtend;
        return result;
    }
}

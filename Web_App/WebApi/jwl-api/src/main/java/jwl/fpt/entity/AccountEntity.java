package jwl.fpt.entity;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "account", schema = "public", catalog = "jwl_test")
public class AccountEntity {
    private String userId;
    private String password;
    private Boolean isInLibrary;
    private Boolean isActivated;
    private UserRoleEntity userRole;
    private ProfileEntity profile;
    private Collection<BorrowedBookCopyEntity> borrowedBookCopies;
    private Collection<BorrowerTicketEntity> borrowerTickets;
    private Collection<WishBookEntity> wishBooks;

    @Id
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "is_in_library")
    public Boolean getInLibrary() {
        return isInLibrary;
    }

    public void setInLibrary(Boolean inLibrary) {
        isInLibrary = inLibrary;
    }

    @Basic
    @Column(name = "is_activated")
    public Boolean getActivated() {
        return isActivated;
    }

    public void setActivated(Boolean activated) {
        isActivated = activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountEntity that = (AccountEntity) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (isInLibrary != null ? !isInLibrary.equals(that.isInLibrary) : that.isInLibrary != null) return false;
        if (isActivated != null ? !isActivated.equals(that.isActivated) : that.isActivated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (isInLibrary != null ? isInLibrary.hashCode() : 0);
        result = 31 * result + (isActivated != null ? isActivated.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    public UserRoleEntity getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRoleEntity userRoleByRoleId) {
        this.userRole = userRoleByRoleId;
    }

    public void setUserRole(Integer roleId) {
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setId(roleId);
        this.userRole = userRoleEntity;
    }

    @OneToMany(mappedBy = "account")
    public Collection<BorrowedBookCopyEntity> getBorrowedBookCopies() {
        return borrowedBookCopies;
    }

    public void setBorrowedBookCopies(Collection<BorrowedBookCopyEntity> borrowedBookCopiesByUserId) {
        this.borrowedBookCopies = borrowedBookCopiesByUserId;
    }

    @OneToMany(mappedBy = "account")
    public Collection<BorrowerTicketEntity> getBorrowerTickets() {
        return borrowerTickets;
    }

    public void setBorrowerTickets(Collection<BorrowerTicketEntity> borrowerTicketsByUserId) {
        this.borrowerTickets = borrowerTicketsByUserId;
    }

    @OneToOne(mappedBy = "account")
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profileByUserId) {
        this.profile = profileByUserId;
    }

    @OneToMany(mappedBy = "account")
    public Collection<WishBookEntity> getWishBooks() {
        return wishBooks;
    }

    public void setWishBooks(Collection<WishBookEntity> wishBooksByUserId) {
        this.wishBooks = wishBooksByUserId;
    }
}

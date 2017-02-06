package jwl.fpt.entity;

import javax.persistence.*;

/**
 * Created by Entaard on 2/5/17.
 */
@Entity
@Table(name = "tbl_user", schema = "public", catalog = "jwl_test")
public class TblUserEntity {
    private Integer userId;
    private String username;
    private String password;
    private Boolean gender;
    private String fullname;

    @Id
    @Column(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
    @Column(name = "gender")
    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "fullname")
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TblUserEntity entity = (TblUserEntity) o;

        if (userId != null ? !userId.equals(entity.userId) : entity.userId != null) return false;
        if (username != null ? !username.equals(entity.username) : entity.username != null) return false;
        if (password != null ? !password.equals(entity.password) : entity.password != null) return false;
        if (gender != null ? !gender.equals(entity.gender) : entity.gender != null) return false;
        if (fullname != null ? !fullname.equals(entity.fullname) : entity.fullname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
        return result;
    }
}

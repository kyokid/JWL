package jwl.fpt.entity;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by Entaard on 2/6/17.
 */
@Entity
@Table(name = "borrower_ticket", schema = "public", catalog = "jwl_test")
public class BorrowerTicketEntity {
    private String qrId;
    private Date createDate;
    private Date scanDate;
    private Date deleteDate;
    private AccountEntity account;

    @Id
    @Column(name = "qr_id")
    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    @Basic
    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "scan_date")
    public Date getScanDate() {
        return scanDate;
    }

    public void setScanDate(Date scanDate) {
        this.scanDate = scanDate;
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

        BorrowerTicketEntity that = (BorrowerTicketEntity) o;

        if (qrId != null ? !qrId.equals(that.qrId) : that.qrId != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (scanDate != null ? !scanDate.equals(that.scanDate) : that.scanDate != null) return false;
        if (deleteDate != null ? !deleteDate.equals(that.deleteDate) : that.deleteDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qrId != null ? qrId.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (scanDate != null ? scanDate.hashCode() : 0);
        result = 31 * result + (deleteDate != null ? deleteDate.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity accountByUserId) {
        this.account = accountByUserId;
    }

    public void setAccount(String userId) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUserId(userId);
        this.account = accountEntity;
    }
}

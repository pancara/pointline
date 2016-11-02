package id.hardana.entity.bni;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by pancara on 1/25/16.
 */

@Entity
@Table(name = "bnibilling")
public class BniBilling implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "virtualaccountid")
    private Long virtualAccountId;

    @Column(name = "transactionid", unique = true)
    private String transactionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expireddatetime")
    private Date expiredDatetime;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "billingType")
    private String billingType;

    @Column(name = "createdatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDatetime;

    @Column(name = "lastupdatedatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDatetime;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private BniBillingStatusEnum status;

    public BniBilling() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVirtualAccountId() {
        return virtualAccountId;
    }

    public void setVirtualAccountId(Long virtualAccountId) {
        this.virtualAccountId = virtualAccountId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getExpiredDatetime() {
        return expiredDatetime;
    }

    public void setExpiredDatetime(Date expiredDate) {
        this.expiredDatetime = expiredDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Date getLastUpdateDatetime() {
        return lastUpdateDatetime;
    }

    public void setLastUpdateDatetime(Date lastUpdateDatetime) {
        this.lastUpdateDatetime = lastUpdateDatetime;
    }

    public BniBillingStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BniBillingStatusEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BniBilling that = (BniBilling) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BniBilling{" +
                "id=" + id +
                ", version=" + version +
                ", virtualAccountId=" + virtualAccountId +
                ", transactionId='" + transactionId + '\'' +
                ", expiredDatetime=" + expiredDatetime +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", amount=" + amount +
                ", createdDatetime=" + createdDatetime +
                ", lastUpdateDatetime=" + lastUpdateDatetime +
                ", status=" + status +
                '}';
    }
}

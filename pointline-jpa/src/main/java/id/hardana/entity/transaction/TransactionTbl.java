/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "transactiontbl")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionTbl.findAll", query = "SELECT t FROM TransactionTbl t"),
    @NamedQuery(name = "TransactionTbl.findById", query = "SELECT t FROM TransactionTbl t WHERE t.id = :id")})
public class TransactionTbl implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Lob
    @Column(name = "referencenumber", unique = true)
    private String referenceNumber;
    @Lob
    @Column(name = "clienttransrefnum")
    private String clientTransRefnum;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private TransactionTypeEnum type;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "fee")
    private BigDecimal fee;
    @Column(name = "totalamount")
    private BigDecimal totalAmount;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private ResponseStatusEnum status;
    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
    @Column(name = "clientdatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date clientDateTime;

    public TransactionTbl() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getClientTransRefnum() {
        return clientTransRefnum;
    }

    public void setClientTransRefnum(String clientTransRefnum) {
        this.clientTransRefnum = clientTransRefnum;
    }

    public TransactionTypeEnum getType() {
        return type;
    }

    public void setType(TransactionTypeEnum type) {
        this.type = type;
    }

    public String getAmount() {
        return amount == null ? null : amount.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee == null ? null : fee.toPlainString();
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getTotalAmount() {
        return totalAmount == null ? null : totalAmount.toPlainString();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ResponseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusEnum status) {
        this.status = status;
    }

    public String getDateTime() {
        return dateTime == null ? null : DATE_FORMAT.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getClientDateTime() {
        return clientDateTime == null ? null : DATE_FORMAT.format(clientDateTime);
    }

    public void setClientDateTime(Date clientDateTime) {
        this.clientDateTime = clientDateTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransactionTbl)) {
            return false;
        }
        TransactionTbl other = (TransactionTbl) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionTbl[ id=" + id + " ]";
    }

}

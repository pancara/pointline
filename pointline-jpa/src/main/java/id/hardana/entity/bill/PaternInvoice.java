/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Trisna
 */
@Entity
@Table(name = "paterninvoice")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PaternInvoice.findAll", query = "SELECT p FROM PaternInvoice p"),
    @NamedQuery(name = "PaternInvoice.findById", query = "SELECT p FROM PaternInvoice p WHERE p.id = :id")})
public class PaternInvoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Version
    private long version;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "number", unique = true)
    private String number;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
    @Column(name = "tablenumber")
    private String tableNumber;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "operatorid")
    private Long operatorId;
    @Column(name = "outletid")
    private Long outletId;

    public PaternInvoice() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAmount() {
        return amount == null ? null : amount.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDateTime() {
        return dateTime == null ? null : DATE_FORMAT.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getOperatorId() {
        return operatorId == null ? null : String.valueOf(operatorId);
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOutletId() {
        return outletId == null ? null : String.valueOf(outletId);
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
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
        if (!(object instanceof PaternInvoice)) {
            return false;
        }
        PaternInvoice other = (PaternInvoice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.bill.PaternInvoice[ id=" + id + " ]";
    }

}

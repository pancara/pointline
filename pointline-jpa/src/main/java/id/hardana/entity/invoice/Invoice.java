/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.invoice;

import id.hardana.entity.invoice.enums.InvoiceStatusEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hanafi
 */
@Entity
@Table(name = "invoice")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Invoice.findAll", query = "SELECT i FROM Invoice i"),
    @NamedQuery(name = "Invoice.findById", query = "SELECT i FROM Invoice i WHERE i.id = :id"),
    @NamedQuery(name = "Invoice.findByMerchantIdWithDateRange", query = "SELECT i FROM Invoice i WHERE i.merchantId = :merchantId "
            + "AND i.dateTime BETWEEN :startDate AND :endDate ")})
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;
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
    @Column(name = "fee")
    private BigDecimal fee;
    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
    @Column(name = "tablenumber")
    private String tableNumber;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private InvoiceStatusEnum status;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "operatorid")
    private Long operatorId;
    @Column(name = "outletid")
    private Long outletId;
    @Column(name = "disbursementid")
    private Long disbursementId;
    @Column(name = "clientdatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date clientDateTime;

    public Invoice() {
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

    public String getFee() {
        return fee == null ? null : fee.toPlainString();
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
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

    public InvoiceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatusEnum status) {
        this.status = status;
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

    public String getDisbursementId() {
        return disbursementId == null ? null : String.valueOf(disbursementId);
    }

    public void setDisbursementId(Long disbursementId) {
        this.disbursementId = disbursementId;
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
        if (!(object instanceof Invoice)) {
            return false;
        }
        Invoice other = (Invoice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.Invoice[ id=" + id + " ]";
    }

}

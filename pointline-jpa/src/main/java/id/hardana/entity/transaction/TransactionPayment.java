/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "transactionpayment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionPayment.findAll", query = "SELECT t FROM TransactionPayment t"),
    @NamedQuery(name = "TransactionPayment.findById", query = "SELECT t FROM TransactionPayment t WHERE t.id = :id"),
    })
public class TransactionPayment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionid")
    private Long transactionId;
    @Column(name = "invoiceid")
    private Long invoiceId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private TransactionPaymentTypeEnum type;
    @Column(name = "sourceid")
    private Long sourceId;

    public TransactionPayment() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId == null ? null : String.valueOf(transactionId);
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getInvoiceId() {
        return invoiceId == null ? null : String.valueOf(invoiceId);
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public TransactionPaymentTypeEnum getType() {
        return type;
    }

    public void setType(TransactionPaymentTypeEnum type) {
        this.type = type;
    }

    public String getSourceId() {
        return sourceId == null ? null : String.valueOf(sourceId);
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
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
        if (!(object instanceof TransactionPayment)) {
            return false;
        }
        TransactionPayment other = (TransactionPayment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionPayment[ id=" + id + " ]";
    }

}

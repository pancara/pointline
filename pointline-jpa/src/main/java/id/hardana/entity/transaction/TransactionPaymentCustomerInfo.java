/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author Trisna
 */
@Entity
@Table(name = "transactionpaymentcustomerinfo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionPaymentCustomerInfo.findAll", query = "SELECT t FROM TransactionPaymentCustomerInfo t"),
    @NamedQuery(name = "TransactionPaymentCustomerInfo.findById", query = "SELECT t FROM TransactionPaymentCustomerInfo t WHERE t.id = :id")})
public class TransactionPaymentCustomerInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionpaymentid")
    private Long transactionPaymentId;
    @Column(name = "customername")
    private String customerName;
    @Column(name = "customeremail")
    private String customerEmail;
    @Column(name = "customerphone")
    private String customerPhone;

    public TransactionPaymentCustomerInfo() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionPaymentId() {
        return transactionPaymentId == null ? null : String.valueOf(transactionPaymentId);
    }

    public void setTransactionPaymentId(Long transactionPaymentId) {
        this.transactionPaymentId = transactionPaymentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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
        if (!(object instanceof TransactionPaymentCustomerInfo)) {
            return false;
        }
        TransactionPaymentCustomerInfo other = (TransactionPaymentCustomerInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionPaymentCustomerInfo[ id=" + id + " ]";
    }

}

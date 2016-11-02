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
 * @author hanafi
 */
@Entity
@Table(name = "transactionpaymentcreditcard")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionPaymentCreditCard.findAll", query = "SELECT t FROM TransactionPaymentCreditCard t"),
    @NamedQuery(name = "TransactionPaymentCreditCard.findById", query = "SELECT t FROM TransactionPaymentCreditCard t WHERE t.id = :id")})
public class TransactionPaymentCreditCard implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionpaymentid")
    private Long transactionPaymentId;
    @Column(name = "cardtype")
    private String cardType;
    @Column(name = "pan")
    private String pan;
    @Column(name = "cardholdername")
    private String cardHolderName;
    @Column(name = "bankid")
    private Long bankId;
    @Column(name = "approvalcode")
    private String approvalCode;

    public TransactionPaymentCreditCard() {
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getBankId() {
        return bankId == null ? null : String.valueOf(bankId);
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
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
        if (!(object instanceof TransactionPaymentCreditCard)) {
            return false;
        }
        TransactionPaymentCreditCard other = (TransactionPaymentCreditCard) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionPaymentCreditCard[ id=" + id + " ]";
    }

}

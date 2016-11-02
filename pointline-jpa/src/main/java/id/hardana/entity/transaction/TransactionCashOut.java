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
@Table(name = "transactioncashout")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionCashOut.findAll", query = "SELECT t FROM TransactionCashOut t"),
    @NamedQuery(name = "TransactionCashOut.findById", query = "SELECT t FROM TransactionCashOut t WHERE t.id = :id")})
public class TransactionCashOut implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionid")
    private Long transactionId;
    @Column(name = "personalinfoid")
    private Long personalInfoId;
    @Column(name = "personalbankid")
    private Long personalBankId;

    public TransactionCashOut() {
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

    public String getPersonalInfoId() {
        return personalInfoId == null ? null : String.valueOf(personalInfoId);
    }

    public void setPersonalInfoId(Long personalInfoId) {
        this.personalInfoId = personalInfoId;
    }

    public String getPersonalBankId() {
        return personalBankId == null ? null : String.valueOf(personalBankId);
    }

    public void setPersonalBankId(Long personalBankId) {
        this.personalBankId = personalBankId;
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
        if (!(object instanceof TransactionCashOut)) {
            return false;
        }
        TransactionCashOut other = (TransactionCashOut) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionCashOut[ id=" + id + " ]";
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import id.hardana.entity.transaction.enums.TransactionPersonalTopupTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "transactionpersonaltopup")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionPersonalTopup.findAll", query = "SELECT t FROM TransactionPersonalTopup t"),
    @NamedQuery(name = "TransactionPersonalTopup.findById", query = "SELECT t FROM TransactionPersonalTopup t WHERE t.id = :id")})
public class TransactionPersonalTopup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionid")
    private Long transactionId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private TransactionPersonalTopupTypeEnum type;
    @Column(name = "personalid")
    private Long personalId;
    @Column(name = "fromid")
    private Long fromId;
    @Column(name = "bankid")
    private Long bankId;
    @Column(name = "banktransactionid")
    private Long bankTransactionId;

    public TransactionPersonalTopup() {
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

    public TransactionPersonalTopupTypeEnum getType() {
        return type;
    }

    public void setType(TransactionPersonalTopupTypeEnum type) {
        this.type = type;
    }

    public String getPersonalId() {
        return personalId == null ? null : String.valueOf(personalId);
    }

    public void setPersonalId(Long personalId) {
        this.personalId = personalId;
    }

    public String getFromId() {
        return fromId == null ? null : String.valueOf(fromId);
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public String getBankId() {
        return bankId == null ? null : String.valueOf(bankId);
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankTransactionId() {
        return bankTransactionId == null ? null : String.valueOf(bankTransactionId);
    }

    public void setBankTransactionId(Long bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
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
        if (!(object instanceof TransactionPersonalTopup)) {
            return false;
        }
        TransactionPersonalTopup other = (TransactionPersonalTopup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionPersonalTopup[ id=" + id + " ]";
    }

}

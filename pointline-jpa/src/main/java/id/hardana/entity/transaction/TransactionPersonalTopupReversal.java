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
@Table(name = "transactionpersonaltopupreversal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionPersonalTopupReversal.findAll", query = "SELECT t FROM TransactionPersonalTopupReversal t"),
    @NamedQuery(name = "TransactionPersonalTopupReversal.findById", query = "SELECT t FROM TransactionPersonalTopupReversal t WHERE t.id = :id")})
public class TransactionPersonalTopupReversal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionidreference")
    private Long transactionIdReference;
    @Column(name = "transactionpersonaltopupid")
    private Long transactionPersonalTopupId;

    public TransactionPersonalTopupReversal() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionIdReference() {
        return transactionIdReference == null ? null : String.valueOf(transactionIdReference);
    }

    public void setTransactionIdReference(Long transactionIdReference) {
        this.transactionIdReference = transactionIdReference;
    }

    public String getTransactionPersonalTopupId() {
        return transactionPersonalTopupId == null ? null : String.valueOf(transactionPersonalTopupId);
    }

    public void setTransactionPersonalTopupId(Long transactionPersonalTopupId) {
        this.transactionPersonalTopupId = transactionPersonalTopupId;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionPersonalTopupReversal[ id=" + id + " ]";
    }

}

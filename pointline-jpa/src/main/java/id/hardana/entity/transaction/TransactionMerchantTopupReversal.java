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
@Table(name = "transactionmerchanttopupreversal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionMerchantTopupReversal.findAll", query = "SELECT t FROM TransactionMerchantTopupReversal t"),
    @NamedQuery(name = "TransactionMerchantTopupReversal.findById", query = "SELECT t FROM TransactionMerchantTopupReversal t WHERE t.id = :id")})
public class TransactionMerchantTopupReversal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionidreference")
    private Long transactionIdReference;
    @Column(name = "transactionmerchanttopupid")
    private Long transactionMerchantTopupId;

    public TransactionMerchantTopupReversal() {
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

    public String getTransactionMerchantTopupId() {
        return transactionMerchantTopupId == null ? null : String.valueOf(transactionMerchantTopupId);
    }

    public void setTransactionMerchantTopupId(Long transactionMerchantTopupId) {
        this.transactionMerchantTopupId = transactionMerchantTopupId;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionMerchantTopupReversal[ id=" + id + " ]";
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "transactionmerchanttopup")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionMerchantTopup.findAll", query = "SELECT t FROM TransactionMerchantTopup t"),
    @NamedQuery(name = "TransactionMerchantTopup.findById", query = "SELECT t FROM TransactionMerchantTopup t WHERE t.id = :id")})
public class TransactionMerchantTopup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionid")
    private Long transactionId;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "outletid")
    private Long outletId;
    @Column(name = "operatorid")
    private Long operatorId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private TransactionMerchantTopupTypeEnum type;
    @Column(name = "topupdestination")
    private Long topupDestination;
    @Column(name = "imbursementid")
    private Long imbursementId;

    public TransactionMerchantTopup() {
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

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getOutletId() {
        return outletId == null ? null : String.valueOf(outletId);
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getOperatorId() {
        return operatorId == null ? null : String.valueOf(operatorId);
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public TransactionMerchantTopupTypeEnum getType() {
        return type;
    }

    public void setType(TransactionMerchantTopupTypeEnum type) {
        this.type = type;
    }

    public String getTopupDestination() {
        return topupDestination == null ? null : String.valueOf(topupDestination);
    }

    public void setTopupDestination(Long topupDestination) {
        this.topupDestination = topupDestination;
    }

    public String getImbursementId() {
        return imbursementId == null ? null : String.valueOf(imbursementId);
    }

    public void setImbursementId(Long imbursementId) {
        this.imbursementId = imbursementId;
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
        if (!(object instanceof TransactionMerchantTopup)) {
            return false;
        }
        TransactionMerchantTopup other = (TransactionMerchantTopup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionMerchantTopup[ id=" + id + " ]";
    }

}

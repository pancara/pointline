/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import id.hardana.entity.transaction.enums.TransactionTransferPersonalToCardTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
@Entity
@Table(name = "transactiontransferpersonaltocard")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionTransferPersonalToCard.findAll", query = "SELECT t FROM TransactionTransferPersonalToCard t"),
    @NamedQuery(name = "TransactionTransferPersonalToCard.findById", query = "SELECT t FROM TransactionTransferPersonalToCard t WHERE t.id = :id")})
public class TransactionTransferPersonalToCard implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactiontransferid")
    private Long transactionTransferId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private TransactionTransferPersonalToCardTypeEnum type;
    @Column(name = "completiondatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completionDateTime;
    @Column(name = "clientcompletiondatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date clientCompletionDateTime;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "outletid")
    private Long outletId;
    @Column(name = "operatorid")
    private Long operatorId;

    public TransactionTransferPersonalToCard() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionTransferId() {
        return transactionTransferId == null ? null : String.valueOf(transactionTransferId);
    }

    public void setTransactionTransferId(Long transactiontransferId) {
        this.transactionTransferId = transactiontransferId;
    }

    public TransactionTransferPersonalToCardTypeEnum getType() {
        return type;
    }

    public void setType(TransactionTransferPersonalToCardTypeEnum type) {
        this.type = type;
    }

    public String getCompletionDateTime() {
        return completionDateTime == null ? null : DATE_FORMAT.format(completionDateTime);
    }

    public void setCompletionDateTime(Date completionDateTime) {
        this.completionDateTime = completionDateTime;
    }

    public String getClientCompletionDateTime() {
        return clientCompletionDateTime == null ? null : DATE_FORMAT.format(clientCompletionDateTime);
    }

    public void setClientCompletionDateTime(Date clientCompletionDateTime) {
        this.clientCompletionDateTime = clientCompletionDateTime;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransactionTransferPersonalToCard)) {
            return false;
        }
        TransactionTransferPersonalToCard other = (TransactionTransferPersonalToCard) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionTransferPersonalToCard[ id=" + id + " ]";
    }

}

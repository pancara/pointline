/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "transactiontransfer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionTransfer.findAll", query = "SELECT t FROM TransactionTransfer t"),
    @NamedQuery(name = "TransactionTransfer.findById", query = "SELECT t FROM TransactionTransfer t WHERE t.id = :id")})
public class TransactionTransfer implements Serializable {

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
    private TransactionTransferTypeEnum type;
    @Column(name = "fromid")
    private Long fromId;
    @Column(name = "toid")
    private Long toId;

    public TransactionTransfer() {
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

    public TransactionTransferTypeEnum getType() {
        return type;
    }

    public void setType(TransactionTransferTypeEnum type) {
        this.type = type;
    }

    public String getFromId() {
        return fromId == null ? null : String.valueOf(fromId);
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId == null ? null : String.valueOf(toId);
    }

    public void setToId(Long toId) {
        this.toId = toId;
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
        if (!(object instanceof TransactionTransfer)) {
            return false;
        }
        TransactionTransfer other = (TransactionTransfer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionTransfer[ id=" + id + " ]";
    }

}

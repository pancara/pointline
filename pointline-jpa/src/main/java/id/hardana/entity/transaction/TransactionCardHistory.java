/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import id.hardana.entity.profile.enums.CardStatusEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Trisna
 */
@Entity
@Table(name = "transactioncardhistory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionCardHistory.findAll", query = "SELECT t FROM TransactionCardHistory t"),
    @NamedQuery(name = "TransactionCardHistory.findById", query = "SELECT t FROM TransactionCardHistory t WHERE t.id = :id"),
    @NamedQuery(name = "TransactionCardHistory.findByTransactionId", query = "SELECT t FROM TransactionCardHistory t WHERE t.transactionId = :transactionId")
    })
public class TransactionCardHistory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "transactionid")
    private Long transactionId;
    @Column(name = "cardid")
    private Long cardId;
    @Column(name = "currentbalancep")
    private BigDecimal currentBalanceP;
    @Column(name = "currentbalances")
    private BigDecimal currentBalanceS;
    @Column(name = "movementp")
    private BigDecimal movementP;
    @Column(name = "movements")
    private BigDecimal movementS;
    @Column(name = "cardstatus")
    private CardStatusEnum cardStatus;

    public TransactionCardHistory() {
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

    public String getCardId() {
        return cardId == null ? null : String.valueOf(cardId);
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCurrentBalanceP() {
        return currentBalanceP == null ? null : currentBalanceP.toPlainString();
    }

    public void setCurrentBalanceP(BigDecimal currentBalanceP) {
        this.currentBalanceP = currentBalanceP;
    }

    public String getCurrentBalanceS() {
        return currentBalanceS == null ? null : currentBalanceS.toPlainString();
    }

    public void setCurrentBalanceS(BigDecimal currentBalanceS) {
        this.currentBalanceS = currentBalanceS;
    }

    public String getMovementP() {
        return movementP == null ? null : movementP.toPlainString();
    }

    public void setMovementP(BigDecimal movementP) {
        this.movementP = movementP;
    }

    public String getMovementS() {
        return movementS == null ? null : movementS.toPlainString();
    }

    public void setMovementS(BigDecimal movementS) {
        this.movementS = movementS;
    }

    public CardStatusEnum getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatusEnum cardStatus) {
        this.cardStatus = cardStatus;
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
        if (!(object instanceof TransactionCardHistory)) {
            return false;
        }
        TransactionCardHistory other = (TransactionCardHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionCardHistory[ id=" + id + " ]";
    }

}

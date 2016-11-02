/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
@Entity
@Table(name = "transactioncardhistoryphysic")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionCardHistoryPhysic.findAll", query = "SELECT t FROM TransactionCardHistoryPhysic t"),
    @NamedQuery(name = "TransactionCardHistoryPhysic.findById", query = "SELECT t FROM TransactionCardHistoryPhysic t WHERE t.id = :id")})
public class TransactionCardHistoryPhysic implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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
    @Column(name = "movementp")
    private BigDecimal movementP;
    @Column(name = "clientdatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date clientDateTime;

    public TransactionCardHistoryPhysic() {
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

    public String getMovementP() {
        return movementP == null ? null : movementP.toPlainString();
    }

    public void setMovementP(BigDecimal movementP) {
        this.movementP = movementP;
    }

    public String getClientDateTime() {
        return clientDateTime == null ? null : DATE_FORMAT.format(clientDateTime);
    }

    public void setClientDateTime(Date clientDateTime) {
        this.clientDateTime = clientDateTime;
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
        if (!(object instanceof TransactionCardHistoryPhysic)) {
            return false;
        }
        TransactionCardHistoryPhysic other = (TransactionCardHistoryPhysic) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionCardHistoryPhysic[ id=" + id + " ]";
    }

}

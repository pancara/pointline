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
@Table(name = "transactioncardhistoryserver")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionCardHistoryServer.findAll", query = "SELECT t FROM TransactionCardHistoryServer t"),
    @NamedQuery(name = "TransactionCardHistoryServer.findById", query = "SELECT t FROM TransactionCardHistoryServer t WHERE t.id = :id")})
public class TransactionCardHistoryServer implements Serializable {

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
    @Column(name = "currentbalances")
    private BigDecimal currentBalanceS;
    @Column(name = "movements")
    private BigDecimal movementS;
    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    public TransactionCardHistoryServer() {
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

    public String getCurrentBalanceS() {
        return currentBalanceS == null ? null : currentBalanceS.toPlainString();
    }

    public void setCurrentBalanceS(BigDecimal currentBalanceS) {
        this.currentBalanceS = currentBalanceS;
    }

    public String getMovementS() {
        return movementS == null ? null : movementS.toPlainString();
    }

    public void setMovementS(BigDecimal movementS) {
        this.movementS = movementS;
    }

    public String getDateTime() {
        return dateTime == null ? null : DATE_FORMAT.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
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
        if (!(object instanceof TransactionCardHistoryServer)) {
            return false;
        }
        TransactionCardHistoryServer other = (TransactionCardHistoryServer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionCardHistoryServer[ id=" + id + " ]";
    }

}

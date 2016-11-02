/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.card;

import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.CardTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hanafi
 */
@Entity
@Table(name = "card")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Card.findAll", query = "SELECT c FROM Card c"),
    @NamedQuery(name = "Card.findById", query = "SELECT c FROM Card c WHERE c.id = :id")})
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "cardid", unique = true)
    private String cardId;
    @Column(name = "pan", unique = true)
    private String pan;
    @Column(name = "pin")
    private String pin;
    @Column(name = "balance")
    private BigDecimal balance;
    @Lob
    @Column(name = "cardholdername")
    private String cardHolderName;
    @Column(name = "issuedate")
    @Temporal(TemporalType.DATE)
    private Date issueDate;
    @Column(name = "expirydate")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    @Lob
    @Column(name = "creditkey")
    private String creditKey;
    @Lob
    @Column(name = "statuskey")
    private String statusKey;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "statusid")
    private CardStatusEnum statusId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "typeid")
    private CardTypeEnum typeId;
    @Version
    private long version;

    public Card() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getBalance() {
        return balance == null ? null : balance.toPlainString();
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getIssueDate() {
        return issueDate == null ? null : DATE_FORMAT.format(issueDate);
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getExpiryDate() {
        return expiryDate == null ? null : DATE_FORMAT.format(expiryDate);
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCreditKey() {
        return creditKey;
    }

    public void setCreditKey(String creditKey) {
        this.creditKey = creditKey;
    }

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    public CardStatusEnum getStatusId() {
        return statusId;
    }

    public void setStatusId(CardStatusEnum statusId) {
        this.statusId = statusId;
    }

    public CardTypeEnum getTypeId() {
        return typeId;
    }

    public void setTypeId(CardTypeEnum typeId) {
        this.typeId = typeId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
        if (!(object instanceof Card)) {
            return false;
        }
        Card other = (Card) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Card[ id=" + id + " ]";
    }

}

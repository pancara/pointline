package id.hardana.entity.document;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by pancara on 12/02/15.
 */
@Entity
@Table(name = "carddata")
public class CardData implements Serializable {

    public final static String TYPE_REGISTERED = "REG";
    public final static String TYPE_UNREGISTERED = "UNREG";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "carddocumentid", nullable = false)
    private Long cardDocumentId;

    @Column(name = "cardid")
    private String cardId;

    @Column(name = "pan")
    private String pan;

    @Column(name = "pin")
    private String pin;

    @Column(name = "cardholdername")
    private String cardHoldername;

    @Column(name = "expirydate")
    private String expiryDate;

    @Column(name = "registrationtype")
    private String registrationType;

    @Column(name = "cardStatus")
    private String cardStatus;

    @Column(name = "registerkey")
    private String registerKey;

    @Column(name = "debetkey")
    private String debetKey;

    @Column(name = "topupkey")
    private String topupKey;


    @Column(name = "success")
    private Boolean success;

    @Column(name = "errorText")
    private String errorText;

    public CardData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardDocumentId() {
        return cardDocumentId;
    }

    public void setCardDocumentId(Long cardDocumentId) {
        this.cardDocumentId = cardDocumentId;
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

    public String getCardHoldername() {
        return cardHoldername;
    }

    public void setCardHoldername(String cardHoldername) {
        this.cardHoldername = cardHoldername;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String regStatus) {
        this.registrationType = regStatus;
    }

    public String getRegisterKey() {
        return registerKey;
    }

    public void setRegisterKey(String registerKey) {
        this.registerKey = registerKey;
    }

    public String getDebetKey() {
        return debetKey;
    }

    public void setDebetKey(String debetKey) {
        this.debetKey = debetKey;
    }

    public String getTopupKey() {
        return topupKey;
    }

    public void setTopupKey(String topupKey) {
        this.topupKey = topupKey;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String status) {
        this.cardStatus = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardData cardData = (CardData) o;

        if (id != null ? !id.equals(cardData.id) : cardData.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

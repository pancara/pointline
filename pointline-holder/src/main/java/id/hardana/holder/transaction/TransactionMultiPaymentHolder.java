/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.transaction;

import id.hardana.entity.profile.card.Card;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class TransactionMultiPaymentHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private TransactionPaymentTypeEnum paymentType;
    private String clientTransRefnum;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalAmount;
    // Credit Card EDC Payment
    private String creditCardType;
    private String creditCardHolderName;
    private String approvalCode;
    // Cash Card Payment
    private String cardUID;
    private BigDecimal debetAmount;
    private BigDecimal cardFinalBalance;
    //Response
    private String referenceNumber;
    private ResponseStatusEnum status;
    private Date transactionDate;
    //Holder Card Object
    private Card card;

    public TransactionMultiPaymentHolder(TransactionPaymentTypeEnum paymentType, String clientTransRefnum,
            BigDecimal amount, BigDecimal fee, BigDecimal totalAmount, String creditCardType,
            String creditCardHolderName, String approvalCode, String cardUID, BigDecimal debetAmount,
            BigDecimal cardFinalBalance) {
        this.paymentType = paymentType;
        this.clientTransRefnum = clientTransRefnum;
        this.amount = amount;
        this.fee = fee;
        this.totalAmount = totalAmount;
        this.creditCardType = creditCardType;
        this.creditCardHolderName = creditCardHolderName;
        this.approvalCode = approvalCode;
        this.cardUID = cardUID;
        this.debetAmount = debetAmount;
        this.cardFinalBalance = cardFinalBalance;
    }

    public TransactionPaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(TransactionPaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
    }

    public String getClientTransRefnum() {
        return clientTransRefnum;
    }

    public void setClientTransRefnum(String clientTransRefnum) {
        this.clientTransRefnum = clientTransRefnum;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
    }

    public String getCreditCardHolderName() {
        return creditCardHolderName;
    }

    public void setCreditCardHolderName(String creditCardHolderName) {
        this.creditCardHolderName = creditCardHolderName;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getCardUID() {
        return cardUID;
    }

    public void setCardUID(String cardUID) {
        this.cardUID = cardUID;
    }

    public BigDecimal getDebetAmount() {
        return debetAmount;
    }

    public void setDebetAmount(BigDecimal debetAmount) {
        this.debetAmount = debetAmount;
    }

    public BigDecimal getCardFinalBalance() {
        return cardFinalBalance;
    }

    public void setCardFinalBalance(BigDecimal cardFinalBalance) {
        this.cardFinalBalance = cardFinalBalance;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public ResponseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusEnum status) {
        this.status = status;
    }

    public String getTransactionDate() {
        return DATE_FORMAT.format(transactionDate);
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

}

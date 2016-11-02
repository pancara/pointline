/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.holder;

import java.math.BigDecimal;

/**
 *
 * @author Trisna
 */
public class TransactionTransferCardHolder {

    private String referenceNumber;
    private BigDecimal amount;
    private Long personalInfoId;
    private Long transactionId;

    public TransactionTransferCardHolder(String referenceNumber, BigDecimal amount, 
            Long personalInfoId, Long transactionId) {
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.personalInfoId = personalInfoId;
        this.transactionId = transactionId;
    }

    public TransactionTransferCardHolder(String referenceNumber, BigDecimal amount) {
        this.referenceNumber = referenceNumber;
        this.amount = amount;
    }

    public TransactionTransferCardHolder(Long personalInfoId, Long transactionId) {
        this.personalInfoId = personalInfoId;
        this.transactionId = transactionId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getPersonalInfoId() {
        return personalInfoId;
    }

    public void setPersonalInfoId(Long personalInfoId) {
        this.personalInfoId = personalInfoId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

}

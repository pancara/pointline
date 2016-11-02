/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.transaction;

import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class TransactionTransferPersonalHistoryHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Long transactionId;
    private String referenceNumber;
    private BigDecimal amount;
    private ResponseStatusEnum status;
    private Date dateTime;
    private String fromAccount;
    private String fromFirstName;
    private String fromLastName;
    private String toAccount;
    private String toFirstName;
    private String toLastName;

    public TransactionTransferPersonalHistoryHolder(Long transactionId, String referenceNumber,
            BigDecimal amount, ResponseStatusEnum status, Date dateTime, String fromAccount,
            String fromFirstName, String fromLastName, String toAccount, String toFirstName,
            String toLastName) {
        this.transactionId = transactionId;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.status = status;
        this.dateTime = dateTime;
        this.fromAccount = fromAccount;
        this.fromFirstName = fromFirstName;
        this.fromLastName = fromLastName;
        this.toAccount = toAccount;
        this.toFirstName = toFirstName;
        this.toLastName = toLastName;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getAmount() {
        return amount.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ResponseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusEnum status) {
        this.status = status;
    }

    public String getDateTime() {
        return DATE_FORMAT.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getFromFirstName() {
        return fromFirstName;
    }

    public void setFromFirstName(String fromFirstName) {
        this.fromFirstName = fromFirstName;
    }

    public String getFromLastName() {
        return fromLastName;
    }

    public void setFromLastName(String fromLastName) {
        this.fromLastName = fromLastName;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String getToFirstName() {
        return toFirstName;
    }

    public void setToFirstName(String toFirstName) {
        this.toFirstName = toFirstName;
    }

    public String getToLastName() {
        return toLastName;
    }

    public void setToLastName(String toLastName) {
        this.toLastName = toLastName;
    }

}

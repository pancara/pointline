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
public class TransactionTransferCardHistoryHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Long transactionId;
    private String referenceNumber;
    private BigDecimal amount;
    private ResponseStatusEnum status;
    private String fromAccount;
    private String fromName;
    private String toPAN;
    private String toName;
    private String viaMerchantName;
    private String viaOutletName;
    private String viaOperatorName;
    private Date completionDate;

    public TransactionTransferCardHistoryHolder(Long transactionId, String referenceNumber,
            BigDecimal amount, ResponseStatusEnum status, String fromAccount, String fromName,
            String toPAN, String toName, String viaMerchantName, String viaOutletName,
            String viaOperatorName, Date completionDate) {
        this.transactionId = transactionId;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.status = status;
        this.fromAccount = fromAccount;
        this.fromName = fromName;
        this.toPAN = toPAN;
        this.toName = toName;
        this.viaMerchantName = viaMerchantName;
        this.viaOutletName = viaOutletName;
        this.viaOperatorName = viaOperatorName;
        this.completionDate = completionDate;
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

    public BigDecimal getAmount() {
        return amount;
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

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToPAN() {
        return toPAN;
    }

    public void setToPAN(String toPAN) {
        this.toPAN = toPAN;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getViaMerchantName() {
        return viaMerchantName;
    }

    public void setViaMerchantName(String viaMerchantName) {
        this.viaMerchantName = viaMerchantName;
    }

    public String getViaOutletName() {
        return viaOutletName;
    }

    public void setViaOutletName(String viaOutletName) {
        this.viaOutletName = viaOutletName;
    }

    public String getViaOperatorName() {
        return viaOperatorName;
    }

    public void setViaOperatorName(String viaOperatorName) {
        this.viaOperatorName = viaOperatorName;
    }

    public String getCompletionDate() {
        return DATE_FORMAT.format(completionDate);
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

}

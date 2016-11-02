/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.holder;

import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPersonalTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferPersonalToCardTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class TransactionPersonalHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
// TransactionTbl
    private Long transactionId;
    private String referenceNumber;
    private String clientTransRefnum;
    private TransactionTypeEnum transactionType;
    private BigDecimal transactionAmount;
    private BigDecimal transactionFee;
    private BigDecimal totalTransactionAmount;
    private ResponseStatusEnum transactionStatus;
    private Date transactionDateTime;
    private String trasactionDebetCredit;
//  TransactionPersonalTopup
    private TransactionPersonalTopupTypeEnum personalTopupType;
// TransactionTransfer
    private TransactionTransferTypeEnum transferType;
    // TransactionTransferToPersonal
    private String transferFromFirstName;
    private String transferFromLastName;
    private String transferFromAccount;
// TransactionTransferToCard
    private String transferToPAN;
    private String transferToCardHolderName;
    private TransactionTransferPersonalToCardTypeEnum transferPersonalToCardType;
    private Date transferCompletionDateTime;
    private String transferViaMerchantName;
    private String transferViaOutletName;
    private double transferViaOutletLatitude;
    private double transferViaOutletLongitude;
    // TransactionTransferToPersonal
    private String transferToFirstName;
    private String transferToLastName;
    private String transferToAccount;
    //  TransactionPayment
    private Long paymentInvoiceId;
    private InvoiceHolder paymentInvoice;
    private TransactionPaymentTypeEnum paymentType;

    public TransactionPersonalHolder(Long transactionId, String referenceNumber, String clientTransRefnum,
            TransactionTypeEnum transactionType, BigDecimal transactionAmount, BigDecimal transactionFee,
            BigDecimal totalTransactionAmount, ResponseStatusEnum transactionStatus,
            Date transactionDateTime, String trasactionDebetCredit,
            TransactionPersonalTopupTypeEnum personalTopupType, TransactionTransferTypeEnum transferType,
            String transferToPAN, String transferToCardHolderName,
            TransactionTransferPersonalToCardTypeEnum transferPersonalToCardType,
            Date transferCompletionDateTime, String transferViaMerchantName,
            String transferViaOutletName, double transferViaOutletLatitude,
            double transferViaOutletLongitude, Long paymentInvoiceId,
            TransactionPaymentTypeEnum paymentType, String transferFromFirstName,
            String transferFromLastName, String transferFromAccount, String transferToFirstName,
            String transferToLastName, String transferToAccount) {
        this.transactionId = transactionId;
        this.referenceNumber = referenceNumber;
        this.clientTransRefnum = clientTransRefnum;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionFee = transactionFee;
        this.totalTransactionAmount = totalTransactionAmount;
        this.transactionStatus = transactionStatus;
        this.transactionDateTime = transactionDateTime;
        this.trasactionDebetCredit = trasactionDebetCredit;
        this.personalTopupType = personalTopupType;
        this.transferType = transferType;
        this.transferToPAN = transferToPAN;
        this.transferToCardHolderName = transferToCardHolderName;
        this.transferPersonalToCardType = transferPersonalToCardType;
        this.transferCompletionDateTime = transferCompletionDateTime;
        this.transferViaMerchantName = transferViaMerchantName;
        this.transferViaOutletName = transferViaOutletName;
        this.transferViaOutletLatitude = transferViaOutletLatitude;
        this.transferViaOutletLongitude = transferViaOutletLongitude;
        this.paymentInvoiceId = paymentInvoiceId;
        this.paymentType = paymentType;
        this.transferFromFirstName = transferFromFirstName;
        this.transferFromLastName = transferFromLastName;
        this.transferFromAccount = transferFromAccount;
        this.transferToFirstName = transferToFirstName;
        this.transferToLastName = transferToLastName;
        this.transferToAccount = transferToAccount;
    }

    public String getTransactionId() {
        return String.valueOf(transactionId);
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

    public String getClientTransRefnum() {
        return clientTransRefnum;
    }

    public void setClientTransRefnum(String clientTransRefnum) {
        this.clientTransRefnum = clientTransRefnum;
    }

    public TransactionTypeEnum getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionAmount() {
        return transactionAmount.toPlainString();
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionFee() {
        return transactionFee.toPlainString();
    }

    public void setTransactionFee(BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String getTotalTransactionAmount() {
        return totalTransactionAmount.toPlainString();
    }

    public void setTotalTransactionAmount(BigDecimal totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
    }

    public ResponseStatusEnum getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(ResponseStatusEnum transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionDateTime() {
        return DATE_FORMAT.format(transactionDateTime);
    }

    public void setTransactionDateTime(Date transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public String getTrasactionDebetCredit() {
        return trasactionDebetCredit;
    }

    public void setTrasactionDebetCredit(String trasactionDebetCredit) {
        this.trasactionDebetCredit = trasactionDebetCredit;
    }

    public TransactionPersonalTopupTypeEnum getPersonalTopupType() {
        return personalTopupType;
    }

    public void setPersonalTopupType(TransactionPersonalTopupTypeEnum personalTopupType) {
        this.personalTopupType = personalTopupType;
    }

    public TransactionTransferTypeEnum getTransferType() {
        return transferType;
    }

    public void setTransferType(TransactionTransferTypeEnum transferType) {
        this.transferType = transferType;
    }

    public String getTransferFromFirstName() {
        return transferFromFirstName;
    }

    public void setTransferFromFirstName(String transferFromFirstName) {
        this.transferFromFirstName = transferFromFirstName;
    }

    public String getTransferFromLastName() {
        return transferFromLastName;
    }

    public void setTransferFromLastName(String transferFromLastName) {
        this.transferFromLastName = transferFromLastName;
    }

    public String getTransferFromAccount() {
        return transferFromAccount;
    }

    public void setTransferFromAccount(String transferFromAccount) {
        this.transferFromAccount = transferFromAccount;
    }

    public String getTransferToPAN() {
        if (TransactionTransferTypeEnum.PERSONALTOCARD.equals(transferType)) {
            return transferToPAN;
        }
        return null;
    }

    public void setTransferToPAN(String transferToPAN) {
        this.transferToPAN = transferToPAN;
    }

    public String getTransferToCardHolderName() {
        if (TransactionTransferTypeEnum.PERSONALTOCARD.equals(transferType)) {
            return transferToCardHolderName;
        }
        return null;
    }

    public void setTransferToCardHolderName(String transferToCardHolderName) {
        this.transferToCardHolderName = transferToCardHolderName;
    }

    public TransactionTransferPersonalToCardTypeEnum getTransferPersonalToCardType() {
        if (TransactionTransferTypeEnum.PERSONALTOCARD.equals(transferType)) {
            return transferPersonalToCardType;
        }
        return null;
    }

    public void setTransferPersonalToCardType(TransactionTransferPersonalToCardTypeEnum transferPersonalToCardType) {
        this.transferPersonalToCardType = transferPersonalToCardType;
    }

    public String getTransferCompletionDateTime() {
        if (TransactionTransferTypeEnum.PERSONALTOCARD.equals(transferType)) {
            return DATE_FORMAT.format(transferCompletionDateTime);
        }
        return null;
    }

    public void setTransferCompletionDateTime(Date transferCompletionDateTime) {
        this.transferCompletionDateTime = transferCompletionDateTime;
    }

    public String getTransferViaMerchantName() {
        if (TransactionTransferTypeEnum.PERSONALTOCARD.equals(transferType)) {
            return transferViaMerchantName;
        }
        return null;
    }

    public void setTransferViaMerchantName(String transferViaMerchantName) {
        this.transferViaMerchantName = transferViaMerchantName;
    }

    public String getTransferViaOutletName() {
        if (TransactionTransferTypeEnum.PERSONALTOCARD.equals(transferType)) {
            return transferViaOutletName;
        }
        return null;
    }

    public void setTransferViaOutletName(String transferViaOutletName) {
        this.transferViaOutletName = transferViaOutletName;
    }

    public String getTransferViaOutletLatitude() {
        if (TransactionTransferTypeEnum.PERSONALTOCARD.equals(transferType)) {
            return String.valueOf(transferViaOutletLatitude);
        }
        return null;
    }

    public void setTransferViaOutletLatitude(double transferViaOutletLatitude) {
        this.transferViaOutletLatitude = transferViaOutletLatitude;
    }

    public String getTransferViaOutletLongitude() {
        if (TransactionTransferTypeEnum.PERSONALTOCARD.equals(transferType)) {
            return String.valueOf(transferViaOutletLongitude);
        }
        return null;
    }

    public void setTransferViaOutletLongitude(double transferViaOutletLongitude) {
        this.transferViaOutletLongitude = transferViaOutletLongitude;
    }

    public String getTransferToFirstName() {
        if (TransactionTransferTypeEnum.PERSONALTOPERSONAL.equals(transferType)) {
            return transferToFirstName;
        }
        return null;
    }

    public void setTransferToFirstName(String transferToFirstName) {
        this.transferToFirstName = transferToFirstName;
    }

    public String getTransferToLastName() {
        if (TransactionTransferTypeEnum.PERSONALTOPERSONAL.equals(transferType)) {
            return transferToLastName;
        }
        return null;
    }

    public void setTransferToLastName(String transferToLastName) {
        this.transferToLastName = transferToLastName;
    }

    public String getTransferToAccount() {
        if (TransactionTransferTypeEnum.PERSONALTOPERSONAL.equals(transferType)) {
            return transferToAccount;
        }
        return null;
    }

    public void setTransferToAccount(String transferToAccount) {
        this.transferToAccount = transferToAccount;
    }

    public Long getPaymentInvoiceId() {
        return paymentInvoiceId;
    }

    public void setPaymentInvoiceId(Long paymentInvoiceId) {
        this.paymentInvoiceId = paymentInvoiceId;
    }

    public InvoiceHolder getPaymentInvoice() {
        return paymentInvoice;
    }

    public void setPaymentInvoice(InvoiceHolder paymentInvoice) {
        this.paymentInvoice = paymentInvoice;
    }

    public TransactionPaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(TransactionPaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.holder;

import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class TransactionCardHolder {

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
//  TransactionPayment
    private Long paymentInvoiceId;
    private InvoiceHolder paymentInvoice;
    private TransactionPaymentTypeEnum paymentType;
// TransactionMerchantTopup
    private String topupMerchantName;
    private String topupOutletName;
    private double topupOutletLatitude;
    private double topupOutletLongitude;
    private TransactionMerchantTopupTypeEnum merchantTopupType;
// TransactionTransfer
    private TransactionTransferTypeEnum transferType;
    private String transferToAccount;
    private String transferToFirstName;
    private String transferToLastName;
    private String transferFromAccount;
    private String transferFromFirstName;
    private String transferFromLastName;
// Card
    private String pan;
    private String cardHolderName;
// Payment Reversal   
    private String referenceNumberReference;

    public TransactionCardHolder(Long transactionId, String referenceNumber, String clientTransRefnum,
            TransactionTypeEnum transactionType, BigDecimal transactionAmount, BigDecimal transactionFee,
            BigDecimal totalTransactionAmount, ResponseStatusEnum transactionStatus,
            Date transactionDateTime, String trasactionDebetCredit, Long paymentInvoiceId,
            String topupMerchantName, String topupOutletName, double topupOutletLatitude,
            double topupOutletLongitude, TransactionTransferTypeEnum transferType,
            String transferFromAccount, String transferFromFirstName, String transferFromLastName,
            String transferToAccount, String transferToFirstName, String transferToLastName,
            String pan, String cardHolderName, TransactionPaymentTypeEnum paymentType,
            TransactionMerchantTopupTypeEnum merchantTopupType, String referenceNumberReference) {
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
        this.paymentInvoiceId = paymentInvoiceId;
        this.topupMerchantName = topupMerchantName;
        this.topupOutletName = topupOutletName;
        this.topupOutletLatitude = topupOutletLatitude;
        this.topupOutletLongitude = topupOutletLongitude;
        this.transferType = transferType;
        this.transferToAccount = transferToAccount;
        this.transferToFirstName = transferToFirstName;
        this.transferToLastName = transferToLastName;
        this.transferFromAccount = transferFromAccount;
        this.transferFromFirstName = transferFromFirstName;
        this.transferFromLastName = transferFromLastName;
        this.pan = pan;
        this.cardHolderName = cardHolderName;
        this.paymentType = paymentType;
        this.merchantTopupType = merchantTopupType;
        this.referenceNumberReference = referenceNumberReference;

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

    public String getPaymentInvoiceId() {
        return String.valueOf(paymentInvoiceId);
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

    public String getTopupMerchantName() {
        return topupMerchantName;
    }

    public void setTopupMerchantName(String topupMerchantName) {
        this.topupMerchantName = topupMerchantName;
    }

    public String getTopupOutletName() {
        return topupOutletName;
    }

    public void setTopupOutletName(String topupOutletName) {
        this.topupOutletName = topupOutletName;
    }

    public String getTopupOutletLatitude() {
        return String.valueOf(topupOutletLatitude);
    }

    public void setTopupOutletLatitude(double topupOutletLatitude) {
        this.topupOutletLatitude = topupOutletLatitude;
    }

    public String getTopupOutletLongitude() {
        return String.valueOf(topupOutletLongitude);
    }

    public void setTopupOutletLongitude(double topupOutletLongitude) {
        this.topupOutletLongitude = topupOutletLongitude;
    }

    public TransactionTransferTypeEnum getTransferType() {
        return transferType;
    }

    public void setTransferType(TransactionTransferTypeEnum transferType) {
        this.transferType = transferType;
    }

    public String getTransferToAccount() {
        return transferToAccount;
    }

    public void setTransferToAccount(String transferToAccount) {
        this.transferToAccount = transferToAccount;
    }

    public String getTransferToFirstName() {
        return transferToFirstName;
    }

    public void setTransferToFirstName(String transferToFirstName) {
        this.transferToFirstName = transferToFirstName;
    }

    public String getTransferToLastName() {
        return transferToLastName;
    }

    public void setTransferToLastName(String transferToLastName) {
        this.transferToLastName = transferToLastName;
    }

    public String getTransferFromAccount() {
        return transferFromAccount;
    }

    public void setTransferFromAccount(String transferFromAccount) {
        this.transferFromAccount = transferFromAccount;
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

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public TransactionPaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(TransactionPaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
    }

    public TransactionMerchantTopupTypeEnum getMerchantTopupType() {
        return merchantTopupType;
    }

    public void setMerchantTopupType(TransactionMerchantTopupTypeEnum merchantTopupType) {
        this.merchantTopupType = merchantTopupType;
    }

    public String getReferenceNumberReference() {
        return referenceNumberReference;
    }

    public void setReferenceNumberReference(String referenceNumberReference) {
        this.referenceNumberReference = referenceNumberReference;
    }

}

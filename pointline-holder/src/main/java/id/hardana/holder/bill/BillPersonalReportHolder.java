/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.bill;

import id.hardana.entity.bill.enums.BillExecutionTypeEnum;
import id.hardana.entity.bill.enums.BillLateFeeTypeEnum;
import id.hardana.entity.bill.enums.BillRejectReasonEnum;
import id.hardana.entity.bill.enums.BillStatusEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class BillPersonalReportHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String billNumber;
    private String billName;
    private BillExecutionTypeEnum executionType;
    private BillStatusEnum billStatus;
    private Date createdDate;
    private Date dueDate;
    private BillLateFeeTypeEnum lateFeeType;
    private BigDecimal lateFeeValue;
    private Integer lateFeeDay;
    private Date paidDate;
    private BigDecimal billAmount;
    private BigDecimal totalLateFeeAmount;
    private BigDecimal totalBillAmount;
    private String info1;
    private String info2;
    private String info3;
    private String payerAccount;
    private String payerFirstName;
    private String payerLastName;
    private String billDescription;
    private String transactionReferenceNumber;
    private BillRejectReasonEnum payerRejectReason;
    private String payerRejectDesc;

    public BillPersonalReportHolder(String billNumber, String billName,
            BillExecutionTypeEnum executionType, BillStatusEnum billStatus, Date createdDate,
            Date dueDate, BillLateFeeTypeEnum lateFeeType, BigDecimal lateFeeValue, Integer lateFeeDay,
            Date paidDate, BigDecimal billAmount, BigDecimal totalLateFeeAmount, BigDecimal totalBillAmount,
            String info1, String info2, String info3, String payerAccount, String payerFirstName,
            String payerLastName, String billDescription, String transactionReferenceNumber,
            BillRejectReasonEnum payerRejectReason, String payerRejectDesc) {
        this.billNumber = billNumber;
        this.billName = billName;
        this.executionType = executionType;
        this.billStatus = billStatus;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.lateFeeType = lateFeeType;
        this.lateFeeValue = lateFeeValue;
        this.lateFeeDay = lateFeeDay;
        this.paidDate = paidDate;
        this.billAmount = billAmount;
        this.totalLateFeeAmount = totalLateFeeAmount;
        this.totalBillAmount = totalBillAmount;
        this.info1 = info1;
        this.info2 = info2;
        this.info3 = info3;
        this.payerAccount = payerAccount;
        this.payerFirstName = payerFirstName;
        this.payerLastName = payerLastName;
        this.billDescription = billDescription;
        this.transactionReferenceNumber = transactionReferenceNumber;
        this.payerRejectReason = payerRejectReason;
        this.payerRejectDesc = payerRejectDesc;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public BillExecutionTypeEnum getExecutionType() {
        return executionType;
    }

    public void setExecutionType(BillExecutionTypeEnum executionType) {
        this.executionType = executionType;
    }

    public BillStatusEnum getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(BillStatusEnum billStatus) {
        this.billStatus = billStatus;
    }

    public String getCreatedDate() {
        return createdDate == null ? null : DATE_FORMAT.format(createdDate);
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDueDate() {
        return dueDate == null ? null : DATE_FORMAT.format(dueDate);
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BillLateFeeTypeEnum getLateFeeType() {
        return lateFeeType;
    }

    public void setLateFeeType(BillLateFeeTypeEnum lateFeeType) {
        this.lateFeeType = lateFeeType;
    }

    public String getLateFeeValue() {
        return lateFeeValue.toPlainString();
    }

    public void setLateFeeValue(BigDecimal lateFeeValue) {
        this.lateFeeValue = lateFeeValue;
    }

    public Integer getLateFeeDay() {
        return lateFeeDay;
    }

    public void setLateFeeDay(Integer lateFeeDay) {
        this.lateFeeDay = lateFeeDay;
    }

    public String getPaidDate() {
        return paidDate == null ? null : DATE_FORMAT.format(paidDate);
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public String getBillAmount() {
        return billAmount.toPlainString();
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    public String getTotalLateFeeAmount() {
        return totalLateFeeAmount.toPlainString();
    }

    public void setTotalLateFeeAmount(BigDecimal totalLateFeeAmount) {
        this.totalLateFeeAmount = totalLateFeeAmount;
    }

    public String getTotalBillAmount() {
        return totalBillAmount.toPlainString();
    }

    public void setTotalBillAmount(BigDecimal totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getInfo3() {
        return info3;
    }

    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    public String getPayerAccount() {
        return payerAccount;
    }

    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }

    public String getPayerFirstName() {
        return payerFirstName;
    }

    public void setPayerFirstName(String payerFirstName) {
        this.payerFirstName = payerFirstName;
    }

    public String getPayerLastName() {
        return payerLastName;
    }

    public void setPayerLastName(String payerLastName) {
        this.payerLastName = payerLastName;
    }

    public String getBillDescription() {
        return billDescription;
    }

    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public BillRejectReasonEnum getPayerRejectReason() {
        return payerRejectReason;
    }

    public void setPayerRejectReason(BillRejectReasonEnum payerRejectReason) {
        this.payerRejectReason = payerRejectReason;
    }

    public String getPayerRejectDesc() {
        return payerRejectDesc;
    }

    public void setPayerRejectDesc(String payerRejectDesc) {
        this.payerRejectDesc = payerRejectDesc;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.bill;

import id.hardana.entity.bill.enums.BillExecutionTypeEnum;
import id.hardana.entity.bill.enums.BillLateFeeTypeEnum;
import id.hardana.entity.bill.enums.BillStatusEnum;
import id.hardana.entity.bill.enums.BillTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class BillPayerReportHolder {

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
    private BillTypeEnum billType;
//    Personal Bill
    private String creatorAccount;
    private String creatorFirstName;
    private String creatorLastName;
    private String billDescription;
    private String transactionReferenceNumber;
//    Merchant Bill
    private String creatorMerchant;
    private String invoiceNumber;

    public BillPayerReportHolder(String billNumber, String billName, BillExecutionTypeEnum executionType,
            BillStatusEnum billStatus, Date createdDate, Date dueDate, BillLateFeeTypeEnum lateFeeType,
            BigDecimal lateFeeValue, Integer lateFeeDay, Date paidDate, BigDecimal billAmount,
            BigDecimal totalLateFeeAmount, BigDecimal totalBillAmount, String info1, String info2,
            String info3, BillTypeEnum billType, String creatorAccount, String creatorFirstName,
            String creatorLastName, String billDescription, String transactionReferenceNumber,
            String creatorMerchant, String invoiceNumber) {
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
        this.billType = billType;
        this.creatorAccount = creatorAccount;
        this.creatorFirstName = creatorFirstName;
        this.creatorLastName = creatorLastName;
        this.billDescription = billDescription;
        this.transactionReferenceNumber = transactionReferenceNumber;
        this.creatorMerchant = creatorMerchant;
        this.invoiceNumber = invoiceNumber;
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

    public BillTypeEnum getBillType() {
        return billType;
    }

    public void setBillType(BillTypeEnum billType) {
        this.billType = billType;
    }

    public String getCreatorAccount() {
        if (BillTypeEnum.PERSONAL_BILL.equals(this.billType)) {
            return creatorAccount;
        }
        return null;
    }

    public void setCreatorAccount(String creatorAccount) {
        this.creatorAccount = creatorAccount;
    }

    public String getCreatorFirstName() {
        if (BillTypeEnum.PERSONAL_BILL.equals(this.billType)) {
            return creatorFirstName;
        }
        return null;
    }

    public void setCreatorFirstName(String creatorFirstName) {
        this.creatorFirstName = creatorFirstName;
    }

    public String getCreatorLastName() {
        if (BillTypeEnum.PERSONAL_BILL.equals(this.billType)) {
            return creatorLastName;
        }
        return null;
    }

    public void setCreatorLastName(String creatorLastName) {
        this.creatorLastName = creatorLastName;
    }

    public String getBillDescription() {
        if (BillTypeEnum.PERSONAL_BILL.equals(this.billType)) {
            return billDescription;
        }
        return null;
    }

    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    public String getTransactionReferenceNumber() {
        if (BillTypeEnum.PERSONAL_BILL.equals(this.billType)) {
            return transactionReferenceNumber;
        }
        return null;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public String getCreatorMerchant() {
        if (BillTypeEnum.MERCHANT_BILL.equals(this.billType)) {
            return creatorMerchant;
        }
        return null;
    }

    public void setCreatorMerchant(String creatorMerchant) {
        this.creatorMerchant = creatorMerchant;
    }

    public String getInvoiceNumber() {
        if (BillTypeEnum.MERCHANT_BILL.equals(this.billType)) {
            return invoiceNumber;
        }
        return null;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

}

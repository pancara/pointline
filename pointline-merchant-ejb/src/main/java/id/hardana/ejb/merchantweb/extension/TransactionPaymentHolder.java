/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class TransactionPaymentHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
// TransactionTbl
    private String referenceNumber;
    private String clientTransRefnum;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalAmount;
    private ResponseStatusEnum status;
    private Date dateTime;
// TransactionPayment
    private TransactionPaymentTypeEnum type;
// Card
    private String pan;
    private String cardHolderName;
// PersonalInfo
    private String account;
    private String firstName;
    private String lastName;
// TransactionPaymentCreditCard
    private String cardType;
    private String approvalCode;

    public TransactionPaymentHolder(TransactionPaymentTypeEnum type, String referenceNumber,
            String clientTransRefnum, BigDecimal amount,
            BigDecimal fee, BigDecimal totalAmount, ResponseStatusEnum status, Date dateTime,
            String pan, String cardHolderName, String account,
            String firstName, String lastName, String cardType, String approvalCode) {
        this.referenceNumber = referenceNumber;
        this.clientTransRefnum = clientTransRefnum;
        this.amount = amount;
        this.fee = fee;
        this.totalAmount = totalAmount;
        this.status = status;
        this.dateTime = dateTime;
        this.type = type;
        this.pan = pan;
        this.cardHolderName = cardHolderName;
        this.account = account;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cardType = cardType;
        this.approvalCode = approvalCode;
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

    public String getAmount() {
        return amount.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee.toPlainString();
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getTotalAmount() {
        return totalAmount.toPlainString();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public TransactionPaymentTypeEnum getType() {
        return type;
    }

    public void setType(TransactionPaymentTypeEnum type) {
        this.type = type;
    }

    public String getPan() {
        if (type != null) {
            if (type.equals(TransactionPaymentTypeEnum.CASHCARD)
                    || type.equals(TransactionPaymentTypeEnum.ADJUSTMENT_CASHCARD)) {
                return pan;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getCardHolderName() {
        if (type != null) {
            if (type.equals(TransactionPaymentTypeEnum.CASHCARD)
                    || type.equals(TransactionPaymentTypeEnum.ADJUSTMENT_CASHCARD)) {
                return cardHolderName;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getAccount() {
        if (type != null) {
            if (type.equals(TransactionPaymentTypeEnum.PERSONAL)) {
                return account;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFirstName() {
        if (type != null) {
            if (type.equals(TransactionPaymentTypeEnum.PERSONAL)) {
                return firstName;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if (type != null) {
            if (type.equals(TransactionPaymentTypeEnum.PERSONAL)) {
                return lastName;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class TransactionMerchantTopupHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
// TransactionTbl
    private String referenceNumber;
    private String clientTransRefnum;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalAmount;
    private ResponseStatusEnum status;
    private Date dateTime;
// TransactionMerchantTopup
    private String operatorName;
    private TransactionMerchantTopupTypeEnum type;
// Card
    private String pan;
    private String cardHolderName;
// PersonalInfo
    private String account;
    private String firstName;
    private String lastName;
//    Outlet
    private String outletName;
    private double outletLatitude;
    private double outletLongitude;

    public TransactionMerchantTopupHolder(String referenceNumber, String clientTransRefnum, BigDecimal amount,
            BigDecimal fee, BigDecimal totalAmount, ResponseStatusEnum status, Date dateTime,
            TransactionMerchantTopupTypeEnum type, String operatorName, String pan, String cardHolderName,
            String account, String firstName, String lastName, String outletName, double outletLatitude, 
            double outletLongitude) {
        this.referenceNumber = referenceNumber;
        this.clientTransRefnum = clientTransRefnum;
        this.amount = amount;
        this.fee = fee;
        this.totalAmount = totalAmount;
        this.status = status;
        this.dateTime = dateTime;
        this.operatorName = operatorName;
        this.type = type;
        this.pan = pan;
        this.cardHolderName = cardHolderName;
        this.account = account;
        this.firstName = firstName;
        this.lastName = lastName;
        this.outletName = outletName;
        this.outletLatitude = outletLatitude;
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

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public TransactionMerchantTopupTypeEnum getType() {
        return type;
    }

    public void setType(TransactionMerchantTopupTypeEnum type) {
        this.type = type;
    }

    public String getPan() {
        if (type != null) {
            if (type.equals(TransactionMerchantTopupTypeEnum.CASHCARD)
                    || type.equals(TransactionMerchantTopupTypeEnum.MANUAL_REVERSAL_CASHCARD)
                    || type.equals(TransactionMerchantTopupTypeEnum.REVERSAL_CASHCARD)) {
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
            if (type.equals(TransactionMerchantTopupTypeEnum.CASHCARD)
                    || type.equals(TransactionMerchantTopupTypeEnum.MANUAL_REVERSAL_CASHCARD)
                    || type.equals(TransactionMerchantTopupTypeEnum.REVERSAL_CASHCARD)) {
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
            if (type.equals(TransactionMerchantTopupTypeEnum.PERSONAL)) {
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
            if (type.equals(TransactionMerchantTopupTypeEnum.PERSONAL)) {
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
            if (type.equals(TransactionMerchantTopupTypeEnum.PERSONAL)) {
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

    public String getOutletName() {
        return outletName;
}

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public double getOutletLatitude() {
        return outletLatitude;
    }

    public void setOutletLatitude(double outletLatitude) {
        this.outletLatitude = outletLatitude;
    }

    public double getOutletLongitude() {
        return outletLongitude;
    }

    public void setOutletLongitude(double outletLongitude) {
        this.outletLongitude = outletLongitude;
    }

}

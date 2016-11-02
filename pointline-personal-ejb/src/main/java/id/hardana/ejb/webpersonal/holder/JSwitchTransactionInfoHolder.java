/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.holder;

import id.hardana.ejb.webpersonal.util.PaymentInfo;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author pancara
 */
public class JSwitchTransactionInfoHolder implements Serializable{
    private String referenfeNumber;
    private String itemName;
    private Date dateTime;
    private BigDecimal totalAmount;
    private List<PaymentInfo> paymentInfo;
    
    public JSwitchTransactionInfoHolder() {
    }

    public String getReferenfeNumber() {
        return referenfeNumber;
    }

    public void setReferenfeNumber(String referenfeNumber) {
        this.referenfeNumber = referenfeNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<PaymentInfo> getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(List<PaymentInfo> paymentInfo) {
        this.paymentInfo = paymentInfo;
    }
    
}

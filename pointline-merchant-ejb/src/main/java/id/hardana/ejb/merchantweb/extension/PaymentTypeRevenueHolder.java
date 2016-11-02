/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import java.math.BigDecimal;

/**
 *
 * @author Trisna
 */
public class PaymentTypeRevenueHolder {

    private TransactionPaymentTypeEnum paymentType;
    private BigDecimal revenue;

    public PaymentTypeRevenueHolder() {
    }

    public PaymentTypeRevenueHolder(TransactionPaymentTypeEnum paymentType, BigDecimal revenue) {
        this.paymentType = paymentType;
        this.revenue = revenue;
    }

    public TransactionPaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(TransactionPaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

}

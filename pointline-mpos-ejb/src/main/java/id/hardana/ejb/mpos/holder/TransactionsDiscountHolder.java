/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.holder;

import java.math.BigDecimal;

/**
 *
 * @author Arya
 */
public class TransactionsDiscountHolder {

    private String transactionDiscountName;
    private BigDecimal transactionDiscountValue;
    private BigDecimal transactionDiscountAmount;
    
    public TransactionsDiscountHolder(){
        
    }

    public TransactionsDiscountHolder(String transactionDiscountName, BigDecimal transactionDiscountValue, BigDecimal transactionDiscountAmount) {
        this.transactionDiscountName = transactionDiscountName;
        this.transactionDiscountValue = transactionDiscountValue;
        this.transactionDiscountAmount = transactionDiscountAmount;
    }
    
    public String getTransactionDiscountName() {
        return transactionDiscountName;
    }

    public void setTransactionDiscountName(String transactionDiscountName) {
        this.transactionDiscountName = transactionDiscountName;
    }

    public BigDecimal getTransactionDiscountValue() {
        return transactionDiscountValue;
    }

    public void setTransactionDiscountValue(BigDecimal transactionDiscountValue) {
        this.transactionDiscountValue = transactionDiscountValue;
    }

    public BigDecimal getTransactionDiscountAmount() {
        return transactionDiscountAmount;
    }

    public void setTransactionDiscountAmount(BigDecimal transactionDiscountAmount) {
        this.transactionDiscountAmount = transactionDiscountAmount;
    }
}

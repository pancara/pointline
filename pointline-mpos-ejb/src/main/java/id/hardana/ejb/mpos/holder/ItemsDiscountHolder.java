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
public class ItemsDiscountHolder {

    private String itemDiscountName;
    private BigDecimal itemDiscountValue;
    private BigDecimal itemDiscountAmount;
    
    public ItemsDiscountHolder(){
        
    }

    public ItemsDiscountHolder(String itemDiscountName, BigDecimal itemDiscountValue, BigDecimal itemDiscountAmount) {
        this.itemDiscountName = itemDiscountName;
        this.itemDiscountValue = itemDiscountValue;
        this.itemDiscountAmount = itemDiscountAmount;
    }

    public String getItemDiscountName() {
        return itemDiscountName;
    }

    public void setItemDiscountName(String itemDiscountName) {
        this.itemDiscountName = itemDiscountName;
    }

    public BigDecimal getItemDiscountValue() {
        return itemDiscountValue;
    }

    public void setItemDiscountValue(BigDecimal itemDiscountValue) {
        this.itemDiscountValue = itemDiscountValue;
    }

    public BigDecimal getItemDiscountAmount() {
        return itemDiscountAmount;
    }

    public void setItemDiscountAmount(BigDecimal itemDiscountAmount) {
        this.itemDiscountAmount = itemDiscountAmount;
    }
    
}

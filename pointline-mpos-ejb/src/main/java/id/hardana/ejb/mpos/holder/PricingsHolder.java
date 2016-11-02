/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.holder;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author Arya
 */
public class PricingsHolder {

    private String pricingName;
    private BigDecimal pricingValue;
    private BigDecimal pricingAmount;
    
    public PricingsHolder(){
        
    }

    public PricingsHolder(String pricingName, BigDecimal pricingValue, BigDecimal pricingAmount) {
        this.pricingName = pricingName;
        this.pricingValue = pricingValue;
        this.pricingAmount = pricingAmount;
    }

    public String getPricingName() {
        return pricingName;
    }

    public void setPricingName(String pricingName) {
        this.pricingName = pricingName;
    }

    public BigDecimal getPricingValue() {
        return pricingValue;
    }

    public void setPricingValue(BigDecimal pricingValue) {
        this.pricingValue = pricingValue;
    }

    public BigDecimal getPricingAmount() {
        return pricingAmount;
    }

    public void setPricingAmount(BigDecimal pricingAmount) {
        this.pricingAmount = pricingAmount;
    }

}

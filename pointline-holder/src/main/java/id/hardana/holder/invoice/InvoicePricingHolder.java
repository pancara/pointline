/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.invoice;

import id.hardana.entity.profile.enums.PricingTypeEnum;
import java.math.BigDecimal;

/**
 *
 * @author Trisna
 */
public class InvoicePricingHolder {

    private Long pricingId;
    private String name;
    private PricingTypeEnum pricingType;
    private BigDecimal pricingValue;
    private BigDecimal pricingAmount;
    private Integer pricingLevel;

    public InvoicePricingHolder() {
    }

    public InvoicePricingHolder(Long pricingId, String name, PricingTypeEnum pricingType,
            BigDecimal pricingValue, BigDecimal pricingAmount, Integer pricingLevel) {
        this.pricingId = pricingId;
        this.name = name;
        this.pricingType = pricingType;
        this.pricingValue = pricingValue;
        this.pricingAmount = pricingAmount;
        this.pricingLevel = pricingLevel;
    }

    public Long getPricingId() {
        return pricingId;
    }

    public void setPricingId(Long pricingId) {
        this.pricingId = pricingId;
    }

    public String getName() {
        if (name == null) {
            name = "Late Fee";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PricingTypeEnum getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingTypeEnum pricingType) {
        this.pricingType = pricingType;
    }

    public String getPricingValue() {
        return pricingValue.toPlainString();
    }

    public void setPricingValue(BigDecimal pricingValue) {
        this.pricingValue = pricingValue;
    }

    public String getPricingAmount() {
        return pricingAmount.toPlainString();
    }

    public void setPricingAmount(BigDecimal pricingAmount) {
        this.pricingAmount = pricingAmount;
    }

    public Integer getPricingLevel() {
        return pricingLevel;
    }

    public void setPricingLevel(Integer pricingLevel) {
        this.pricingLevel = pricingLevel;
    }

}

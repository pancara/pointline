/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.invoice;

import java.math.BigDecimal;

/**
 *
 * @author pancara
 */
public class InvoiceItemDiscountHolder {
     
    private Long discountId;
    
    private String discountName;
    
    private String discountDescription;
    
    private BigDecimal discountValue;
    
    private Integer discountValueType;
    
    private Integer discountCalculationType;
    
    private Integer discountApplyType;
    
    private BigDecimal discountAmount;

    public InvoiceItemDiscountHolder() {
    }

    public InvoiceItemDiscountHolder(Long discountId, String discountName, String discountDescription, 
            BigDecimal discountValue, Integer discountValueType, 
            Integer discountCalculationType, Integer discountApplyType, 
            BigDecimal discountAmount) {
        this.discountId = discountId;
        this.discountName = discountName;
        this.discountDescription = discountDescription;
        this.discountValue = discountValue;
        this.discountValueType = discountValueType;
        this.discountCalculationType = discountCalculationType;
        this.discountApplyType = discountApplyType;
        this.discountAmount = discountAmount;
    }
    
    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getDiscountValueType() {
        return discountValueType;
    }

    public void setDiscountValueType(Integer discountValueType) {
        this.discountValueType = discountValueType;
    }

    public Integer getDiscountCalculationType() {
        return discountCalculationType;
    }

    public void setDiscountCalculationType(Integer discountCalculationType) {
        this.discountCalculationType = discountCalculationType;
    }

    public Integer getDiscountApplyType() {
        return discountApplyType;
    }

    public void setDiscountApplyType(Integer discountApplyType) {
        this.discountApplyType = discountApplyType;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    
    
}

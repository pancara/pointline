/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill;

import id.hardana.entity.profile.enums.PricingTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Trisna
 */
@Entity
@Table(name = "paterninvoicepricing")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "PaternInvoicePricing.findAll", query = "SELECT i FROM PaternInvoicePricing i"),
        @NamedQuery(name = "PaternInvoicePricing.findById", query = "SELECT i FROM PaternInvoicePricing i WHERE i.id = :id")})
public class PaternInvoicePricing implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "invoiceid")
    private Long invoiceId;
    @Column(name = "pricingid")
    private Long pricingId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "pricingtype")
    private PricingTypeEnum pricingType;
    @Column(name = "pricingvalue")
    private BigDecimal pricingValue;
    @Column(name = "pricingamount")
    private BigDecimal pricingAmount;
    @Column(name = "pricinglevel")
    private Integer pricingLevel;

    public PaternInvoicePricing() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceId() {
        return invoiceId == null ? null : String.valueOf(invoiceId);
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPricingId() {
        return pricingId == null ? null : String.valueOf(pricingId);
    }

    public void setPricingId(Long pricingId) {
        this.pricingId = pricingId;
    }

    public PricingTypeEnum getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingTypeEnum pricingType) {
        this.pricingType = pricingType;
    }

    public String getPricingValue() {
        return pricingValue == null ? null : pricingValue.toPlainString();
    }

    public void setPricingValue(BigDecimal pricingValue) {
        this.pricingValue = pricingValue;
    }

    public String getPricingAmount() {
        return pricingAmount == null ? null : pricingAmount.toPlainString();
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
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaternInvoicePricing)) {
            return false;
        }
        PaternInvoicePricing other = (PaternInvoicePricing) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.bill.PaternInvoicePricing[ id=" + id + " ]";
    }

}

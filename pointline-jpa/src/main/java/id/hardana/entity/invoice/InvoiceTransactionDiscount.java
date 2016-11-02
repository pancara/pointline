/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.invoice;

import id.hardana.entity.profile.enums.DiscountApplyTypeEnum;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author pancara
 * 
 */
@Entity
@Table(name = "invoicetransactiondiscount")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvoiceTransactionDiscount.findByInvoiceId", query = "SELECT i FROM InvoiceTransactionDiscount i WHERE i.invoiceId = :invoiceId"),
    })
public class InvoiceTransactionDiscount implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
 
    @Column(name = "invoiceid")
    private Long invoiceId;
    
    @Column(name = "discountid")
    private Long discountId;
    
    @Size(max = 50)
    @Column(name = "discountname")
    private String discountName;
    
    @Size(max = 1000)
    @Column(name = "discountdescription")
    private String discountDescription;
    
    @Column(name = "discountvalue", precision = 38, scale = 2)
    private BigDecimal discountValue;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "discountvaluetype")
    private DiscountValueTypeEnum discountValueType;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "discountcalculationtype")
    private DiscountCalculationTypeEnum discountCalculationType;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "discountapplytype")
    private DiscountApplyTypeEnum discountApplyType;
    
    
    @Column(name = "discountamount", precision = 38, scale = 2)
    private BigDecimal discountAmount;
    
    public InvoiceTransactionDiscount() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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

    public DiscountValueTypeEnum getDiscountValueType() {
        return discountValueType;
    }

    public void setDiscountValueType(DiscountValueTypeEnum discountValueType) {
        this.discountValueType = discountValueType;
    }

    public DiscountCalculationTypeEnum getDiscountCalculationType() {
        return discountCalculationType;
    }

    public void setDiscountCalculationType(DiscountCalculationTypeEnum discountCalculationType) {
        this.discountCalculationType = discountCalculationType;
    }

    public DiscountApplyTypeEnum getDiscountApplyType() {
        return discountApplyType;
    }

    public void setDiscountApplyType(DiscountApplyTypeEnum discountApplyType) {
        this.discountApplyType = discountApplyType;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
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
        if (!(object instanceof InvoiceTransactionDiscount)) {
            return false;
        }
        InvoiceTransactionDiscount other = (InvoiceTransactionDiscount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "InvoiceTransactionDiscount{" + "id=" + id + ", invoiceId=" + invoiceId + ", discountId=" + discountId + ", discountName=" + discountName + ", discountDescription=" + discountDescription + ", discountValue=" + discountValue + ", discountValueType=" + discountValueType + ", discountCalculationType=" + discountCalculationType + ", discountApplyType=" + discountApplyType + ", discountAmount=" + discountAmount + '}';
    }


}

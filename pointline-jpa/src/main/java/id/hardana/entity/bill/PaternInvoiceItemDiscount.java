package id.hardana.entity.bill;

import id.hardana.entity.profile.enums.DiscountApplyTypeEnum;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Trisna
 *
 */
@Entity
@Table(name = "paterninvoiceitemdiscount")
public class PaternInvoiceItemDiscount implements Serializable {

    private static final long serialVersionUID = 1L;
    @Version
    private long version;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "invoiceitemid")
    private Long invoiceItemId;
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

    public PaternInvoiceItemDiscount() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceItemId() {
        return invoiceItemId;
    }

    public void setInvoiceItemId(Long invoiceItemId) {
        this.invoiceItemId = invoiceItemId;
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
        if (!(object instanceof PaternInvoiceItemDiscount)) {
            return false;
        }
        PaternInvoiceItemDiscount other = (PaternInvoiceItemDiscount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PaternInvoiceItemDiscount{" + "id=" + id + ", invoiceItemId=" + invoiceItemId + ", discountId=" + discountId + ", discountName=" + discountName + ", discountDescription=" + discountDescription + ", discountValue=" + discountValue + ", discountValueType=" + discountValueType + ", discountCalculationType=" + discountCalculationType + ", discountApplyType=" + discountApplyType + ", discountAmount=" + discountAmount + '}';
    }

}

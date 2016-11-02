package id.hardana.entity.profile.merchant;


import id.hardana.entity.profile.enums.DiscountApplyTypeEnum;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author pancara
 */

@Entity
@Table(name = "discount")
public class Discount implements Serializable {
   
    private static final long serialVersionUID = 1L;
    
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Size(max = 50)
    @Column(name = "name")
    private String name;
    
    @Size(max = 1000)
    @Column(name = "description")
    private String description;
    
    @Size(max = 12)
    @Column(name = "color")
    private String color;
    
    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "auto")
    private Boolean auto;
    
    @Column(name = "merchantid")
    private Long merchantId;
    
    @Column(name = "value", precision = 38, scale = 2)
    private BigDecimal value;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "valueType")
    private DiscountValueTypeEnum valueType;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "calculationtype")
    private DiscountCalculationTypeEnum calculationType;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "applytype")
    private DiscountApplyTypeEnum applyType;
    
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    
    @Column(name = "isdeleted")
    private Boolean isDeleted;

    public Discount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }
    
    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public DiscountValueTypeEnum getValueType() {
        return valueType;
    }

    public void setValueType(DiscountValueTypeEnum valueType) {
        this.valueType = valueType;
    }

    public DiscountCalculationTypeEnum getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(DiscountCalculationTypeEnum calculationType) {
        this.calculationType = calculationType;
    }

    public DiscountApplyTypeEnum getApplyType() {
        return applyType;
    }

    public void setApplyType(DiscountApplyTypeEnum applyType) {
        this.applyType = applyType;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Discount)) {
            return false;
        }
        Discount other = (Discount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Discount{" + "id=" + id + ", name=" + name + ", description=" + description + ", color=" + color + ", enabled=" + enabled + ", auto=" + auto + ", merchantId=" + merchantId + ", value=" + value + ", valueType=" + valueType + ", calculationType=" + calculationType + ", applyType=" + applyType + ", lastUpdated=" + lastUpdated + ", isDeleted=" + isDeleted + '}';
    }
    
    
    
}

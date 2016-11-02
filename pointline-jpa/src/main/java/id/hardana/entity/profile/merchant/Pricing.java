/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

import id.hardana.entity.profile.enums.PricingTypeEnum;
import id.hardana.entity.profile.enums.ProductStatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hanafi
 */
@Entity
@Table(name = "pricing")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Pricing.findAll", query = "SELECT p FROM Pricing p"),
        @NamedQuery(name = "Pricing.findById", query = "SELECT p FROM Pricing p WHERE p.id = :id"),
        @NamedQuery(name = "Pricing.findByMerchantId", query = "SELECT p FROM Pricing p WHERE p.merchantId = :merchantId"),
        @NamedQuery(name = "Pricing.findByMerchantIdNotDeleted", query = "SELECT p FROM Pricing p WHERE p.merchantId = :merchantId AND p.isDeleted=false")})
public class Pricing implements Serializable {

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
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private PricingTypeEnum type;
    @Column(name = "pricingvalue", precision = 38, scale = 2)
    private BigDecimal pricingValue;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private ProductStatusEnum status;
    @Column(name = "merchantid")
    private Long merchantId;
    
    @Column(name = "level")
    private Integer level = 1;
    
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @Column(name = "isdeleted")
    private Boolean isDeleted;

    public Pricing() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
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

    public PricingTypeEnum getType() {
        return type;
    }

    public void setType(PricingTypeEnum type) {
        this.type = type;
    }

    public String getPricingValue() {
        return pricingValue == null ? null : pricingValue.toPlainString();
    }

    public void setPricingValue(BigDecimal pricingValue) {
        this.pricingValue = pricingValue;
    }

    public ProductStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ProductStatusEnum status) {
        this.status = status;
    }

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public String getLastUpdated() {
        return lastUpdated == null ? null : DATE_FORMAT.format(lastUpdated);
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getIsDeleted() {
        return isDeleted == null ? null : String.valueOf(isDeleted);
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pricing)) {
            return false;
        }
        Pricing other = (Pricing) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.Pricing[ id=" + id + " ]";
    }

}

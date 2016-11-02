/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

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
@Table(name = "items")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Items.findAll", query = "SELECT i FROM Items i"),
        @NamedQuery(name = "Items.findById", query = "SELECT i FROM Items i WHERE i.id = :id"),
        @NamedQuery(name = "Items.findItemsByMerchantId", query = "SELECT i FROM Items i WHERE i.merchantId = :merchantId"),
        @NamedQuery(name = "Items.findByCategoryId", query = "SELECT i FROM Items i WHERE i.categoryId = :categoryId"),
        @NamedQuery(name = "Items.findByCategoryIdNotDeleted", query = "SELECT i FROM Items i WHERE i.categoryId = :categoryId AND i.isDeleted=false"),
        @NamedQuery(name = "Items.findByCodeAndMerchantIdNotDeleted", query = "SELECT i FROM Items i WHERE i.code = :code AND i.merchantId = :merchantId ")
})
public class Items implements Serializable {

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
    @Size(max = 20)
    @Column(name = "code")
    private String code;
    @Column(name = "categoryid")
    private Long categoryId;
    @Column(name = "merchantId")
    private Long merchantId;
    @Column(name = "supplyprice")
    private BigDecimal supplyPrice;
    @Column(name = "markupprice")
    private BigDecimal markupPrice;
    @Column(name = "salesprice")
    private BigDecimal salesPrice;
    @Lob
    @Column(name = "picture")
    private String picture;
    @Lob
    @Column(name = "description")
    private String description;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private ProductStatusEnum status;
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @Column(name = "isdeleted")
    private Boolean isDeleted;

    public Items() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategoryId() {
        return categoryId == null ? null : String.valueOf(categoryId);
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the merchantId
     */
    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    /**
     * @param merchantId the merchantId to set
     */
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getSupplyPrice() {
        return supplyPrice == null ? null : supplyPrice.toPlainString();
    }

    public void setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public String getMarkupPrice() {
        return markupPrice == null ? null : markupPrice.toPlainString();
    }

    public void setMarkupPrice(BigDecimal markupPrice) {
        this.markupPrice = markupPrice;
    }

    public String getSalesPrice() {
        return salesPrice == null ? null : salesPrice.toPlainString();
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ProductStatusEnum status) {
        this.status = status;
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
        if (!(object instanceof Items)) {
            return false;
        }
        Items other = (Items) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.Items[ id=" + id + " ]";
    }

}

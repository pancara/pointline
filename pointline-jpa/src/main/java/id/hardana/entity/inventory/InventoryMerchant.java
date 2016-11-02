/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.inventory;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Trisna
 */
@Entity
@Table(name = "inventorymerchant")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InventoryMerchant.findAll", query = "SELECT i FROM InventoryMerchant i"),
    @NamedQuery(name = "InventoryMerchant.findById", query = "SELECT i FROM InventoryMerchant i WHERE i.id = :id")})
public class InventoryMerchant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @Column(name = "updatedby")
    private Long updatedBy;
    @Column(name = "isactive")
    private Boolean isActive;

    public InventoryMerchant() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getUpdatedBy() {
        return updatedBy == null ? null : String.valueOf(updatedBy);
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getLastUpdated() {
        return lastUpdated == null ? null : DATE_FORMAT.format(lastUpdated);
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getIsActive() {
        return isActive == null ? null : String.valueOf(isActive);
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        if (!(object instanceof InventoryMerchant)) {
            return false;
        }
        InventoryMerchant other = (InventoryMerchant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.inventory.InventoryMerchant[ id=" + id + " ]";
    }

}

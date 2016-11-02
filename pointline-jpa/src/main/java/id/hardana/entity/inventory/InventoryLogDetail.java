/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.inventory;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Trisna
 */
@Entity
@Table(name = "inventorylogdetail")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InventoryLogDetail.findAll", query = "SELECT i FROM InventoryLogDetail i"),
    @NamedQuery(name = "InventoryLogDetail.findById", query = "SELECT i FROM InventoryLogDetail i WHERE i.id = :id"),
    @NamedQuery(name = "InventoryLogDetail.findByInventoryLogId", query = "SELECT i FROM InventoryLogDetail i WHERE i.inventoryLogId IN :inventoryLogId ")})
public class InventoryLogDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "inventorylogid")
    private Long inventoryLogId;
    @Column(name = "itemid")
    private Long itemId;
    @Column(name = "outletid")
    private Long outletId;
    @Lob
    @Column(name = "itemName")
    private String itemName;
    @Column(name = "itemquantity")
    private Long itemQuantity;
    @Column(name = "description")
    private String description;

    public InventoryLogDetail() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInventoryLogId() {
        return inventoryLogId == null ? null : String.valueOf(inventoryLogId);
    }

    public void setInventoryLogId(Long inventoryLogId) {
        this.inventoryLogId = inventoryLogId;
    }

    public String getItemId() {
        return itemId == null ? null : String.valueOf(itemId);
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getOutletId() {
        return outletId == null ? null : String.valueOf(outletId);
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity == null ? null : String.valueOf(itemQuantity);
    }

    public void setItemQuantity(Long itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(object instanceof InventoryLogDetail)) {
            return false;
        }
        InventoryLogDetail other = (InventoryLogDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.inventory.InventoryLogDetail[ id=" + id + " ]";
    }

}

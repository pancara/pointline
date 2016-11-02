/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Trisna
 */
@Entity
@Table(name = "paterninvoiceitems")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PaternInvoiceItems.findAll", query = "SELECT i FROM PaternInvoiceItems i"),
    @NamedQuery(name = "PaternInvoiceItems.findById", query = "SELECT i FROM PaternInvoiceItems i WHERE i.id = :id"),
    @NamedQuery(name = "PaternInvoiceItems.findByInvoiceId", query = "SELECT i FROM PaternInvoiceItems i WHERE i.invoiceId in :invoiceIds")})
public class PaternInvoiceItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "invoiceid")
    private Long invoiceId;
    @Column(name = "itemid")
    private Long itemId;
    @Lob
    @Column(name = "itemName")
    private String itemName;
    @Column(name = "itemsupplyprice")
    private BigDecimal itemSupplyPrice;
    @Column(name = "itemsalesprice")
    private BigDecimal itemSalesPrice;
    @Column(name = "itemquantity")
    private Integer itemQuantity;
    @Column(name = "itemsubtotal")
    private BigDecimal itemSubTotal;

    public PaternInvoiceItems() {
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

    public String getItemId() {
        return itemId == null ? null : String.valueOf(itemId);
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemSupplyPrice() {
        return itemSupplyPrice == null ? null : itemSupplyPrice.toPlainString();
    }

    public void setItemSupplyPrice(BigDecimal itemSupplyPrice) {
        this.itemSupplyPrice = itemSupplyPrice;
    }

    public String getItemSalesPrice() {
        return itemSalesPrice == null ? null : itemSalesPrice.toPlainString();
    }

    public void setItemSalesPrice(BigDecimal itemSalesPrice) {
        this.itemSalesPrice = itemSalesPrice;
    }

    public String getItemQuantity() {
        return itemQuantity == null ? null : String.valueOf(itemQuantity);
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemSubTotal() {
        return itemSubTotal == null ? null : itemSubTotal.toPlainString();
    }

    public void setItemSubTotal(BigDecimal itemSubTotal) {
        this.itemSubTotal = itemSubTotal;
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
        if (!(object instanceof PaternInvoiceItems)) {
            return false;
        }
        PaternInvoiceItems other = (PaternInvoiceItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.bill.PaternInvoiceItems[ id=" + id + " ]";
    }

}

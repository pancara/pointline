/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.inventory;

import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna Wanto
 */
@Entity
@Table(name = "inventorylog")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InventoryLog.findAll", query = "SELECT i FROM InventoryLog i"),
    @NamedQuery(name = "InventoryLog.findById", query = "SELECT i FROM InventoryLog i WHERE i.id = :id")})
public class InventoryLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private InventoryLogTypeEnum type;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "outletid")
    private Long outletId;
    @Column(name = "operatorid")
    private Long operatorId;
    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
    @Column(name = "clientdatetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date clientDateTime;
    @Lob
    @Column(name = "description")
    private String description;
    @Column(name = "invoiceid")
    private Long invoiceId;

    public InventoryLog() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InventoryLogTypeEnum getType() {
        return type;
    }

    public void setType(InventoryLogTypeEnum type) {
        this.type = type;
    }

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getOutletId() {
        return outletId == null ? null : String.valueOf(outletId);
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getOperatorId() {
        return operatorId == null ? null : String.valueOf(operatorId);
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getDateTime() {
        return dateTime == null ? null : DATE_FORMAT.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getClientDateTime() {
        return clientDateTime == null ? null : DATE_FORMAT.format(clientDateTime);
    }

    public void setClientDateTime(Date clientDateTime) {
        this.clientDateTime = clientDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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
        if (!(object instanceof InventoryLog)) {
            return false;
        }
        InventoryLog other = (InventoryLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.inventory.InventoryLog[ id=" + id + " ]";
    }

}

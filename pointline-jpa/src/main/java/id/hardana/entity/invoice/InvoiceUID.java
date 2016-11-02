/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.invoice;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
@Entity
@Table(name = "invoiceuid")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvoiceUID.findAll", query = "SELECT i FROM InvoiceUID i"),
    @NamedQuery(name = "InvoiceUID.findById", query = "SELECT i FROM InvoiceUID i WHERE i.id = :id")})
public class InvoiceUID implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "outletid")
    private Long outletId;
    @Column(name = "uniqueid")
    private String uniqueId;
    @Column(name = "generateddatetime")
    @Temporal(TemporalType.DATE)
    private Date generatedDate;
    @Column(name = "isused")
    private Boolean isUsed;
    @Column(name = "invoiceid")
    private Long invoiceId;

    public InvoiceUID() {
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

    public String getOutletId() {
        return outletId == null ? null : String.valueOf(outletId);
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getGeneratedDate() {
        return generatedDate == null ? null : DATE_FORMAT.format(generatedDate);
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getIsUsed() {
        return isUsed == null ? null : String.valueOf(isUsed);
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public String getInvoiceId() {
        return invoiceId == null ? null : String.valueOf(invoiceId);
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
        if (!(object instanceof InvoiceUID)) {
            return false;
        }
        InvoiceUID other = (InvoiceUID) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionUID[ id=" + id + " ]";
    }

}

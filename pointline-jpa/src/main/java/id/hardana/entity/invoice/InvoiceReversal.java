/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.invoice;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Arya
 */
@Entity
@Table(name = "InvoiceReversal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvoiceReversal.findAll", query = "SELECT i FROM InvoiceReversal i"),
    @NamedQuery(name = "InvoiceReversal.findById", query = "SELECT i FROM InvoiceReversal i WHERE i.id = :id")})

public class InvoiceReversal implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "invoiceid")
    private Long invoiceId;
    @Column(name = "invoiceIdReference")
    private Long invoiceIdReference;

    public InvoiceReversal() {
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

    public Long getInvoiceIdReference() {
        return invoiceIdReference;
    }

    public void setInvoiceIdReference(Long invoiceIdReference) {
        this.invoiceIdReference = invoiceIdReference;
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
        if (!(object instanceof InvoiceReversal)) {
            return false;
        }
        InvoiceReversal other = (InvoiceReversal) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.InvoiceReversal[ id=" + id + " ]";
    }

}

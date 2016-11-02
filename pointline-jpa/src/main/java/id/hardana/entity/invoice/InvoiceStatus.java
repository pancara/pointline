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
 * @author hanafi
 */
@Entity
@Table(name = "invoicestatus")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "InvoiceStatus.findAll", query = "SELECT i FROM InvoiceStatus i"),
        @NamedQuery(name = "InvoiceStatus.findById", query = "SELECT i FROM InvoiceStatus i WHERE i.id = :id")})
public class InvoiceStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "status", unique = true)
    private String status;

    public InvoiceStatus() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        if (!(object instanceof InvoiceStatus)) {
            return false;
        }
        InvoiceStatus other = (InvoiceStatus) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.InvoiceStatus[ id=" + id + " ]";
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Trisna
 */
@Entity
@Table(name = "billlatefeetype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BillLateFeeType.findAll", query = "SELECT b FROM BillLateFeeType b"),
    @NamedQuery(name = "BillLateFeeType.findById", query = "SELECT b FROM BillLateFeeType b WHERE b.id = :id")})
public class BillLateFeeType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "type", unique = true)
    private String type;

    public BillLateFeeType() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        if (!(object instanceof BillLateFeeType)) {
            return false;
        }
        BillLateFeeType other = (BillLateFeeType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.bill.BillLateFeeType[ id=" + id + " ]";
    }

}

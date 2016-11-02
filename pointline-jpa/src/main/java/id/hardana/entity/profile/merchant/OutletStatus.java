/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "outletstatus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OutletStatus.findAll", query = "SELECT o FROM OutletStatus o"),
    @NamedQuery(name = "OutletStatus.findById", query = "SELECT o FROM OutletStatus o WHERE o.id = :id")})
public class OutletStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "status", unique = true)
    private String status;

    public OutletStatus() {
    }

    public String getId() {
        return String.valueOf(id);
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
        if (!(object instanceof OutletStatus)) {
            return false;
        }
        OutletStatus other = (OutletStatus) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.OutletStatus[ id=" + id + " ]";
    }

}

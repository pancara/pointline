/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.personal;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "identitytype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IdentityType.findAll", query = "SELECT i FROM IdentityType i"),
    @NamedQuery(name = "IdentityType.findById", query = "SELECT i FROM IdentityType i WHERE i.id = :id")})
public class IdentityType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Lob
    @Column(name = "type", unique = true)
    private String type;

    public IdentityType() {
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
        if (!(object instanceof IdentityType)) {
            return false;
        }
        IdentityType other = (IdentityType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.IdentityType[ id=" + id + " ]";
    }

}

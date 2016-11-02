/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.other;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author hanafi
 */
@Entity
@Table(name = "province")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Province.findAll", query = "SELECT p FROM Province p"),
        @NamedQuery(name = "Province.findById", query = "SELECT p FROM Province p WHERE p.id = :id")})
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Long id;
    @Lob
    @Column(name = "name", unique = true)
    private String name;

    public Province() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof Province)) {
            return false;
        }
        Province other = (Province) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Province[ id=" + id + " ]";
    }

}

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
@Table(name = "lineofbusiness")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LineOfBusiness.findAll", query = "SELECT l FROM LineOfBusiness l"),
    @NamedQuery(name = "LineOfBusiness.findById", query = "SELECT l FROM LineOfBusiness l WHERE l.id = :id")})
public class LineOfBusiness implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;
    @Lob
    @Column(name = "lineofbusiness")
    private String lineOfBusiness;

    public LineOfBusiness() {
    }

    public LineOfBusiness(Integer id) {
        this.id = id;
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
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
        if (!(object instanceof LineOfBusiness)) {
            return false;
        }
        LineOfBusiness other = (LineOfBusiness) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.LineOfBusiness[ id=" + id + " ]";
    }

}

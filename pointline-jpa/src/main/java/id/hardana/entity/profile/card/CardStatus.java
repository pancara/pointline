/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.card;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author hanafi
 */
@Entity
@Table(name = "cardstatus")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "CardStatus.findAll", query = "SELECT c FROM CardStatus c"),
        @NamedQuery(name = "CardStatus.findById", query = "SELECT c FROM CardStatus c WHERE c.id = :id")})
public class CardStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "status", unique = true)
    private String status;

    public CardStatus() {
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
        if (!(object instanceof CardStatus)) {
            return false;
        }
        CardStatus other = (CardStatus) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.CardStatus[ id=" + id + " ]";
    }

}

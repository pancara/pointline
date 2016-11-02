/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.personal;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "personaltype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonalType.findAll", query = "SELECT p FROM PersonalType p"),
    @NamedQuery(name = "PersonalType.findById", query = "SELECT p FROM PersonalType p WHERE p.id = :id")})
public class PersonalType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "type", unique = true)
    private String type;
    @Column(name = "limitbalance")
    private BigDecimal limitBalance;

    public PersonalType() {
    }

    public String getId() {
        return String.valueOf(id);
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

    public String getLimitBalance() {
        return limitBalance.toPlainString();
    }

    public void setLimitBalance(BigDecimal limitBalance) {
        this.limitBalance = limitBalance;
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
        if (!(object instanceof PersonalType)) {
            return false;
        }
        PersonalType other = (PersonalType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.PersonalType[ id=" + id + " ]";
    }

}

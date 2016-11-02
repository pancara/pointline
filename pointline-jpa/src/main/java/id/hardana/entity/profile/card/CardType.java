/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.card;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author hanafi
 */
@Entity
@Table(name = "cardtype")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "CardType.findAll", query = "SELECT c FROM CardType c"),
        @NamedQuery(name = "CardType.findById", query = "SELECT c FROM CardType c WHERE c.id = :id")})
public class CardType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "type", unique = true)
    private String type;
    @Column(name = "limitbalance")
    private BigDecimal limitBalance;

    public CardType() {
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

    public String getLimitBalance() {
        return limitBalance == null ? null : limitBalance.toPlainString();
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
        if (!(object instanceof CardType)) {
            return false;
        }
        CardType other = (CardType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.CardType[ id=" + id + " ]";
    }

}

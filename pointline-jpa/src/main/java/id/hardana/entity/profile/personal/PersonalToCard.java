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
@Table(name = "personaltocard")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonalToCard.findAll", query = "SELECT p FROM PersonalToCard p"),
    @NamedQuery(name = "PersonalToCard.findById", query = "SELECT p FROM PersonalToCard p WHERE p.id = :id")})
public class PersonalToCard implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "cardid")
    private Long cardId;
    @Column(name = "personalinfoid")
    private Long personalInfoId;

    public PersonalToCard() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId == null ? null : String.valueOf(cardId);
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getPersonalInfoId() {
        return personalInfoId == null ? null : String.valueOf(personalInfoId);
    }

    public void setPersonalInfoId(Long personalInfoId) {
        this.personalInfoId = personalInfoId;
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
        if (!(object instanceof PersonalToCard)) {
            return false;
        }
        PersonalToCard other = (PersonalToCard) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.PersonalToCard[ id=" + id + " ]";
    }

}

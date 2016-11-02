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
 * @author Trisna
 */
@Entity
@Table(name = "personalvirtualaccountbloked")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonalVirtualAccountBlocked.findAll", query = "SELECT p FROM PersonalVirtualAccountBlocked p"),
    @NamedQuery(name = "PersonalVirtualAccountBlocked.findById", query = "SELECT p FROM PersonalVirtualAccountBlocked p WHERE p.id = :id"),
    @NamedQuery(name = "PersonalVirtualAccountBlocked.findByPersonalInfoId", query = "SELECT p FROM PersonalVirtualAccountBlocked p WHERE p.personalInfoId = :personalInfoId"),
    @NamedQuery(name = "PersonalVirtualAccountBlocked.findByPersonalVirtualAccountId", query = "SELECT p FROM PersonalVirtualAccountBlocked p WHERE p.personalVirtualAccountId = :personalVirtualAccountId")})
public class PersonalVirtualAccountBlocked implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "personalinfoid")
    private Long personalInfoId;
    @Column(name = "personalvirtualaccountid")
    private Long personalVirtualAccountId;
    @Column(name = "isblocked")
    private Boolean isBlocked;

    public PersonalVirtualAccountBlocked() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonalInfoId() {
        return personalInfoId == null ? null : String.valueOf(personalInfoId);
    }

    public void setPersonalInfoId(Long personalInfoId) {
        this.personalInfoId = personalInfoId;
    }

    public String getPersonalVirtualAccountId() {
        return personalVirtualAccountId == null ? null : String.valueOf(personalVirtualAccountId);
    }

    public void setPersonalVirtualAccountId(Long personalVirtualAccountId) {
        this.personalVirtualAccountId = personalVirtualAccountId;
    }

    public String getIsBlocked() {
        return isBlocked == null ? null : String.valueOf(isBlocked);
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
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
        if (!(object instanceof PersonalVirtualAccountBlocked)) {
            return false;
        }
        PersonalVirtualAccountBlocked other = (PersonalVirtualAccountBlocked) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.PersonalVirtualAccountBlocked[ id=" + id + " ]";
    }

}

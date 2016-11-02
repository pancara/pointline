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
@Table(name = "personalbank")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonalBank.findAll", query = "SELECT p FROM PersonalBank p"),
    @NamedQuery(name = "PersonalBank.findById", query = "SELECT p FROM PersonalBank p WHERE p.id = :id")})
public class PersonalBank implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Lob
    @Column(name = "bankbranch")
    private String bankBranch;
    @Lob
    @Column(name = "accountholdername")
    private String accountHolderName;
    @Lob
    @Column(name = "accountnumber")
    private String accountNumber;
    @Column(name = "bankid")
    private Long bankId;
    @Column(name = "personalinfoid")
    private Long personalInfoId;
    @Column(name = "isdeleted")
    private Boolean isDeleted;

    public PersonalBank() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankId() {
        return bankId == null ? null : String.valueOf(bankId);
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getPersonalInfoId() {
        return personalInfoId == null ? null : String.valueOf(personalInfoId);
    }

    public void setPersonalInfoId(Long personalInfoId) {
        this.personalInfoId = personalInfoId;
    }

    public String getIsDeleted() {
        return isDeleted == null ? null : String.valueOf(isDeleted);
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
        if (!(object instanceof PersonalBank)) {
            return false;
        }
        PersonalBank other = (PersonalBank) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.PersonalBank[ id=" + id + " ]";
    }

}

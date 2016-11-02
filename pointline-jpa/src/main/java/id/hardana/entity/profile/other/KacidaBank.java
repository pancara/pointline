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
 *
 * @author hanafi
 */
@Entity
@Table(name = "kacidabank")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "KacidaBank.findAll", query = "SELECT k FROM KacidaBank k"),
    @NamedQuery(name = "KacidaBank.findById", query = "SELECT k FROM KacidaBank k WHERE k.id = :id")})
public class KacidaBank implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Lob
    @Column(name = "accountnumber")
    private String accountNumber;
    @Lob
    @Column(name = "bankbranch")
    private String bankBranch;
    @Lob
    @Column(name = "accholdername")
    private String accHolderName;
    @Column(name = "bankid")
    private Long bankId;

    public KacidaBank() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getAccHolderName() {
        return accHolderName;
    }

    public void setAccHolderName(String accHolderName) {
        this.accHolderName = accHolderName;
    }

    public String getBankId() {
        return bankId == null ? null : String.valueOf(bankId);
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
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
        if (!(object instanceof KacidaBank)) {
            return false;
        }
        KacidaBank other = (KacidaBank) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.KacidaBank[ id=" + id + " ]";
    }

}

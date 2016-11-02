/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.personal;

import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.enums.PersonalTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "personal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Personal.findAll", query = "SELECT p FROM Personal p"),
    @NamedQuery(name = "Personal.findById", query = "SELECT p FROM Personal p WHERE p.id = :id"),
    @NamedQuery(name = "Personal.findByPersonalInfoId", query = "SELECT p FROM Personal p WHERE p.personalInfoId = :personalInfoId")})
public class Personal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "personalinfoid", unique = true)
    private Long personalInfoId;
    @Column(name = "password1")
    private String password1;
    @Column(name = "password2")
    private String password2;
    @Column(name = "password3")
    private String password3;
    @Column(name = "passwordexpired")
    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordExpired;
    @Column(name = "pin")
    private String pin;
    @Column(name = "actcode")
    private String activationCode;
    @Column(name = "balance")
    private BigDecimal balance;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "statusid")
    private PersonalStatusEnum statusId;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "typeid")
    private PersonalTypeEnum typeId;
    @Column(name = "secretquestionid")
    private Integer secretQuestionId;
    @Lob
    @Column(name = "secretanswer")
    private String secretAnswer;
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastupdated;
    @Column(name = "isdeleted")
    private Boolean isDeleted;
    @Version
    private long version;

    public Personal() {
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

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getPassword3() {
        return password3;
    }

    public void setPassword3(String password3) {
        this.password3 = password3;
    }

    public String getPasswordExpired() {
        return passwordExpired == null ? null : DATE_FORMAT.format(passwordExpired);
    }

    public void setPasswordExpired(Date passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getBalance() {
        return balance == null ? null : balance.toPlainString();
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public PersonalStatusEnum getStatusId() {
        return statusId;
    }

    public void setStatusId(PersonalStatusEnum statusId) {
        this.statusId = statusId;
    }

    public PersonalTypeEnum getTypeId() {
        return typeId;
    }

    public void setTypeId(PersonalTypeEnum typeid) {
        this.typeId = typeid;
    }

    public String getSecretQuestionId() {
        return secretQuestionId == null ? null : String.valueOf(secretQuestionId);
    }

    public void setSecretQuestionId(Integer secretQuestionId) {
        this.secretQuestionId = secretQuestionId;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getLastupdated() {
        return lastupdated == null ? null : DATE_FORMAT.format(lastupdated);
    }

    public void setLastupdated(Date lastupdated) {
        this.lastupdated = lastupdated;
    }

    public String getIsDeleted() {
        return isDeleted == null ? null : String.valueOf(isDeleted);
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
        if (!(object instanceof Personal)) {
            return false;
        }
        Personal other = (Personal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Personal[ id=" + id + " ]";
    }

}

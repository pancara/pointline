/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

import id.hardana.entity.profile.enums.OperatorStatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hanafi
 */
@Entity
@Table(name = "operator")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Operator.findAll", query = "SELECT o FROM Operator o"),
        @NamedQuery(name = "Operator.findById", query = "SELECT o FROM Operator o WHERE o.id = :id"),
        @NamedQuery(name = "Operator.findByMerchantId", query = "SELECT o FROM Operator o WHERE o.merchantId = :merchantId"),
        @NamedQuery(name = "Operator.findByUserNameAndMerchantId", query = "SELECT o FROM Operator o WHERE o.userName = :userName AND o.merchantId = :merchantId"),
        @NamedQuery(name = "Operator.getOpId", query = "SELECT o FROM Operator o WHERE o.merchantId = :merchantId AND o.userName = :userName")})
public class Operator implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 30)
    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "fullname")
    private String fullName;
    @Lob
    @Column(name = "profilepic")
    private String profilePic;
    @Column(name = "employeenumber")
    private String employeeNumber;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "operatorlevelid")
    private Long operatorLevelId;
    @Column(name = "isowner")
    private Boolean isOwner;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private OperatorStatusEnum status;
    @Column(name = "isdeleted")
    private Boolean isDeleted;
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Transient
    private String operatorLevelName;

    public Operator() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getOperatorLevelId() {
        return operatorLevelId == null ? null : String.valueOf(operatorLevelId);
    }

    public void setOperatorLevelId(Long operatorLevelId) {
        this.operatorLevelId = operatorLevelId;
    }

    public String getIsOwner() {
        return isOwner == null ? null : String.valueOf(isOwner);
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public OperatorStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OperatorStatusEnum status) {
        this.status = status;
    }

    public String getIsDeleted() {
        return isDeleted == null ? null : String.valueOf(isDeleted);
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getLastUpdated() {
        return lastUpdated == null ? null : DATE_FORMAT.format(lastUpdated);
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * @return the operatorLevelName
     */
    @Transient
    public String getOperatorLevelName() {
        return operatorLevelName;
    }

    /**
     * @param operatorLevelName the operatorLevelName to set
     */
    public void setOperatorLevelName(String operatorLevelName) {
        this.operatorLevelName = operatorLevelName;
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
        if (!(object instanceof Operator)) {
            return false;
        }
        Operator other = (Operator) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Operator[ id=" + id + " ]";
    }

}

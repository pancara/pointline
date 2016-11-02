/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.group;

import id.hardana.entity.profile.enums.GroupOperatorStatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Arya
 */@Entity
@Table(name = "groupoperator")
@XmlRootElement
@NamedQueries({
         @NamedQuery(name = "GroupOperator.findAll", query = "SELECT o FROM GroupOperator o"),
         @NamedQuery(name = "GroupOperator.findById", query = "SELECT o FROM GroupOperator o WHERE o.id = :id"),
         @NamedQuery(name = "GroupOperator.findByGroupId", query = "SELECT o FROM GroupOperator o WHERE o.groupId = :groupId"),
         @NamedQuery(name = "GroupOperator.findByUserNameAndGroupId", query = "SELECT o FROM GroupOperator o WHERE o.groupUserName = :groupUserName AND o.groupId = :groupId"),
         @NamedQuery(name = "GroupOperator.getGroupOperatorId", query = "SELECT o FROM GroupOperator o WHERE o.groupId = :groupId AND o.groupUserName = :groupUserName")})

 public class GroupOperator implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 30)
    @Column(name = "groupusername")
    private String groupUserName;
    @Column(name = "password")
    private String password;
    @Column(name = "fullname")
    private String fullName;
    @Lob
    @Column(name = "employeenumber")
    private String employeeNumber;
    @Column(name = "groupid")
    private Long groupId;
    @Column(name = "isowner")
    private Boolean isOwner;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private GroupOperatorStatusEnum status;
    @Column(name = "isdeleted")
    private Boolean isDeleted;
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    public GroupOperator() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupUserName() {
        return groupUserName;
    }

    public void setGroupUserName(String groupUserName) {
        this.groupUserName = groupUserName;
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

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Boolean isIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public GroupOperatorStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GroupOperatorStatusEnum status) {
        this.status = status;
    }

    public Boolean isDeleted() {
        return isDeleted;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupOperator)) {
            return false;
        }
        GroupOperator other = (GroupOperator) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.GroupOperator[ id=" + id + " ]";
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.group;

import id.hardana.entity.profile.enums.GroupMerchantToMerchantStatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Arya
 */
@Entity
@Table(name = "groupmerchanttomerchant")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "GroupMerchantToMerchant.findAll", query = "SELECT gmm FROM GroupMerchantToMerchant gmm"),
        @NamedQuery(name = "GroupMerchantToMerchant.findByGroupId", query = "SELECT gmm FROM GroupMerchantToMerchant gmm WHERE gmm.groupId = :groupId"),
        @NamedQuery(name = "GroupMerchantToMerchant.findMerchantIdByGroupId", query = "SELECT gmm.merchantId FROM GroupMerchantToMerchant gmm WHERE gmm.groupId = :groupId"),
        @NamedQuery(name = "GroupMerchantToMerchant.findByGroupIdAndMerchantId", query = "SELECT gmm FROM GroupMerchantToMerchant gmm WHERE gmm.groupId = :groupId AND gmm.merchantId = :merchantId"),
        @NamedQuery(name = "GroupMerchantToMerchant.deleteByMerchantCode", query = "DELETE FROM GroupMerchantToMerchant WHERE groupid = :groupId AND merchantCode = :merchantCode")
})

public class GroupMerchantToMerchant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 50)
    @Column(name = "groupId")
    private String groupId;
    @Column(name = "merchantCode")
    private String merchantCode;
    @Column(name = "linkdaterequest")
    @Temporal(TemporalType.TIMESTAMP)
    private Date linkDateRequest;
    @Column(name = "merchantid")
    private Long merchantId;
    @Lob
    @Column(name = "brandname")
    private String brandName;
    @Column(name = "status")
    private GroupMerchantToMerchantStatusEnum status;
    @Column(name = "linkdateresponse")
    @Temporal(TemporalType.TIMESTAMP)
    private Date linkDateResponse;
    @Column(name = "dateupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;
     
    public GroupMerchantToMerchant() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }
    
    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public GroupMerchantToMerchantStatusEnum getStatus() {
        return status;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    
    public void setStatus(GroupMerchantToMerchantStatusEnum status) {
        this.status = status;
    }

    public String getLinkDateRequest() {
        return linkDateRequest == null ? null : DATE_FORMAT.format(linkDateRequest);
    }

    public void setLinkDateRequest(Date linkDateRequest) {
        this.linkDateRequest = linkDateRequest;
    }
    public String getDateUpdated() {
        return dateUpdated == null ? null : DATE_FORMAT.format(dateUpdated);
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
    
    public String getLinkDateResponse() {
        return linkDateResponse == null ? null : DATE_FORMAT.format(linkDateResponse);
    }

    public void setLinkDateResponse(Date linkDateResponse) {
        this.linkDateResponse = linkDateResponse;
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
        if (!(object instanceof GroupMerchantToMerchant)) {
            return false;
        }
        GroupMerchantToMerchant other = (GroupMerchantToMerchant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.GroupMerchantToMerchant[ id=" + id + " ]";
    }

}

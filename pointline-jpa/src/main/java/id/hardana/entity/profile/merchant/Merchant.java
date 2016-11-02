/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

import id.hardana.entity.profile.enums.IdentityTypeEnum;
import id.hardana.entity.profile.enums.MerchantStatusEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hanafi
 */
@Entity
@Table(name = "merchant")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Merchant.findAll", query = "SELECT m FROM Merchant m"),
        @NamedQuery(name = "Merchant.findById", query = "SELECT m FROM Merchant m WHERE m.id = :id"),
        @NamedQuery(name = "Merchant.findByCode", query = "SELECT m FROM Merchant m WHERE m.code = :code")})
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "code", unique = true)
    private String code;
    @Column(name = "companyid")
    private Long companyId;
    @Lob
    @Column(name = "brandname")
    private String brandName;
    @Lob
    @Column(name = "branddescription")
    private String brandDescription;
    @Lob
    @Column(name = "majorproduct")
    private String majorProduct;
    @Column(name = "lob")
    private Long lob;
    @Size(max = 50)
    @Column(name = "ownername")
    private String ownerName;
    @Size(max = 25)
    @Column(name = "ownerphone")
    private String ownerPhone;
    @Lob
    @Column(name = "owneremail")
    private String ownerEmail;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "owneridtype")
    private IdentityTypeEnum ownerIdType;
    @Column(name = "owneridnumber")
    private String ownerIdNumber;
    @Size(max = 50)
    @Column(name = "contactname")
    private String contactName;
    @Size(max = 25)
    @Column(name = "contactphone")
    private String contactPhone;
    @Lob
    @Column(name = "contactemail")
    private String contactEmail;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "contactidtype")
    private IdentityTypeEnum contactIdType;
    @Column(name = "contactidnumber")
    private String contactIdNumber;
    @Column(name = "bankid")
    private Long bankId;
    @Lob
    @Column(name = "bankbranch")
    private String bankBranch;
    @Column(name = "bankaccountnumber")
    private String bankAccountNumber;
    @Column(name = "bankaccountname")
    private String bankAccountName;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private MerchantStatusEnum status;
    @Column(name = "merchantfee")
    private BigDecimal merchantFee;
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    public Merchant() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompanyId() {
        return companyId == null ? null : String.valueOf(companyId);
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandDescription() {
        return brandDescription;
    }

    public void setBrandDescription(String brandDescription) {
        this.brandDescription = brandDescription;
    }

    public String getMajorProduct() {
        return majorProduct;
    }

    public void setMajorProduct(String majorProduct) {
        this.majorProduct = majorProduct;
    }

    public String getLob() {
        return lob == null ? null : String.valueOf(lob);
    }

    public void setLob(Long lob) {
        this.lob = lob;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public IdentityTypeEnum getOwnerIdType() {
        return ownerIdType;
    }

    public void setOwnerIdType(IdentityTypeEnum ownerIdType) {
        this.ownerIdType = ownerIdType;
    }

    public String getOwnerIdNumber() {
        return ownerIdNumber;
    }

    public void setOwnerIdNumber(String ownerIdNumber) {
        this.ownerIdNumber = ownerIdNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String reprEmail) {
        this.contactEmail = reprEmail;
    }

    public IdentityTypeEnum getContactIdType() {
        return contactIdType;
    }

    public void setContactIdType(IdentityTypeEnum reprIdType) {
        this.contactIdType = reprIdType;
    }

    public String getContactIdNumber() {
        return contactIdNumber;
    }

    public void setContactIdNumber(String contactIdNumber) {
        this.contactIdNumber = contactIdNumber;
    }

    public String getBankId() {
        return bankId == null ? null : String.valueOf(bankId);
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public MerchantStatusEnum getStatus() {
        return status;
    }

    public void setStatus(MerchantStatusEnum status) {
        this.status = status;
    }

    public String getMerchantFee() {
        return merchantFee == null ? null : merchantFee.toPlainString();
    }

    public void setMerchantFee(BigDecimal merchantFee) {
        this.merchantFee = merchantFee;
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
        if (!(object instanceof Merchant)) {
            return false;
        }
        Merchant other = (Merchant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Merchant[ id=" + id + " ]";
    }

}

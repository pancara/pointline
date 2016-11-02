/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.personal;

import id.hardana.entity.profile.enums.IdentityTypeEnum;

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
@Table(name = "personalinfo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonalInfo.findAll", query = "SELECT p FROM PersonalInfo p"),
    @NamedQuery(name = "PersonalInfo.findById", query = "SELECT p FROM PersonalInfo p WHERE p.id = :id"),
    @NamedQuery(name = "PersonalInfo.findByAccount", query = "SELECT p FROM PersonalInfo p WHERE p.account = :account")})
public class PersonalInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 30)
    @Column(name = "account", unique = true)
    private String account;
    @Size(max = 30)
    @Column(name = "firstname")
    private String firstName;
    @Size(max = 30)
    @Column(name = "lastname")
    private String lastName;
    @Column(name = "gender")
    private Character gender;
    @Lob
    @Column(name = "email")
    private String email;
    @Size(max = 5)
    @Column(name = "zipcode")
    private String zipCode;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "identitytype")
    private IdentityTypeEnum identityType;
    @Size(max = 30)
    @Column(name = "idnumber")
    private String idNumber;
    @Lob
    @Column(name = "profilepic")
    private String profilePic;
    @Size(max = 20)
    @Column(name = "npwp")
    private String npwp;
    @Lob
    @Column(name = "signature")
    private String signature;
    @Column(name = "dateofbirth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Size(max = 50)
    @Column(name = "placeofbirth")
    private String placeOfBirth;
    @Lob
    @Column(name = "identitypicture")
    private String identityPicture;
    @Column(name = "regdate")
    @Temporal(TemporalType.DATE)
    private Date regDate;
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @Column(name = "cityId")
    private Long cityId;
    @Column(name = "address")
    private String address;
    @Version
    private long version;

    public PersonalInfo() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender == null ? null : String.valueOf(gender);
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public IdentityTypeEnum getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityTypeEnum identityType) {
        this.identityType = identityType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDateOfBirth() {
        return dateOfBirth == null ? null : DATE_FORMAT.format(dateOfBirth);
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getIdentityPicture() {
        return identityPicture;
    }

    public void setIdentityPicture(String identityPicture) {
        this.identityPicture = identityPicture;
    }

    public String getRegDate() {
        return regDate == null ? null : DATE_FORMAT.format(regDate);
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getLastUpdated() {
        return lastUpdated == null ? null : DATE_FORMAT.format(lastUpdated);
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getCityId() {
        return cityId == null ? null : String.valueOf(cityId);
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        if (!(object instanceof PersonalInfo)) {
            return false;
        }
        PersonalInfo other = (PersonalInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.PersonalInfo[ id=" + id + " ]";
    }

}

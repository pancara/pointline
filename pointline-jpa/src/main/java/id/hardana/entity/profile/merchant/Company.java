/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author hanafi
 */
@Entity
@Table(name = "company")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Company.findAll", query = "SELECT c FROM Company c"),
        @NamedQuery(name = "Company.findById", query = "SELECT c FROM Company c WHERE c.id = :id"),
        @NamedQuery(name = "Company.findByCompanyCode", query = "SELECT c FROM Company c WHERE c.akta = :companyCode"),
        @NamedQuery(name = "Company.checkPassword", query = "SELECT c FROM Company c WHERE c.code = :companyCode AND c.password = :password ")})
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "code", unique = true)
    private String code;
    @Column(name = "password")
    private String password;
    @Lob
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "address")
    private String address;
    @Size(max = 30)
    @Column(name = "siup")
    private String siup;
    @Size(max = 30)
    @Column(name = "akta")
    private String akta;
    @Size(max = 20)
    @Column(name = "npwp")
    private String npwp;
    @Size(max = 6)
    @Column(name = "zipcode")
    private String zipCode;
    @Size(max = 25)
    @Column(name = "phone")
    private String phone;
    @Lob
    @Column(name = "email")
    private String email;
    @Size(max = 50)
    @Column(name = "owner")
    private String owner;
    @Column(name = "cityid")
    private Long cityId;

    public Company() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSiup() {
        return siup;
    }

    public void setSiup(String siup) {
        this.siup = siup;
    }

    public String getAkta() {
        return akta;
    }

    public void setAkta(String akta) {
        this.akta = akta;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCityId() {
        return cityId == null ? null : String.valueOf(cityId);
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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
        if (!(object instanceof Company)) {
            return false;
        }
        Company other = (Company) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Company[ id=" + id + " ]";
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

import id.hardana.entity.profile.enums.OutletStatusEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hanafi
 */
@Entity
@Table(name = "outlet")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Outlet.findAll", query = "SELECT o FROM Outlet o"),
        @NamedQuery(name = "Outlet.findById", query = "SELECT o FROM Outlet o WHERE o.id = :id"),
        @NamedQuery(name = "Outlet.findByMerchantId", query = "SELECT o FROM Outlet o WHERE o.merchantId = :merchantId")})
public class Outlet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "address")
    private String address;
    @Column(name = "cityid")
    private Long cityId;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "longitude")
    private double longitude;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private OutletStatusEnum status;
    @Column(name = "isdeleted")
    private Boolean isDeleted;
    @Column(name = "lastupdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Transient
    private String cityName;
    @Transient
    private String provinceId;

    public Outlet() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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

    public String getCityId() {
        return cityId == null ? null : String.valueOf(cityId);
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getLatitude() {
        return String.valueOf(latitude);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return String.valueOf(longitude);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public OutletStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OutletStatusEnum status) {
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
     * @return the cityName
     */
    @Transient
    public String getCityName() {
        return cityName;
    }

    /**
     * @param cityName the cityName to set
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * @return the provinceId
     */
    @Transient
    public String getProvinceId() {
        return provinceId;
    }

    /**
     * @param provinceId the provinceId to set
     */
    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
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
        if (!(object instanceof Outlet)) {
            return false;
        }
        Outlet other = (Outlet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Outlet[ id=" + id + " ]";
    }

}

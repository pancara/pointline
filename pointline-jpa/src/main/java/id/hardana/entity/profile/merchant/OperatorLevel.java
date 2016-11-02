/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author hanafi
 */
@Entity
@Table(name = "operatorlevel")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "OperatorLevel.findAll", query = "SELECT o FROM OperatorLevel o"),
        @NamedQuery(name = "OperatorLevel.findById", query = "SELECT o FROM OperatorLevel o WHERE o.id = :id"),
        @NamedQuery(name = "OperatorLevel.findByMerchantId", query = "SELECT o FROM OperatorLevel o WHERE o.merchantId = :merchantId")})
public class OperatorLevel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "merchantid")
    private Long merchantId;
    @Lob
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "description")
    private String description;

    public OperatorLevel() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(object instanceof OperatorLevel)) {
            return false;
        }
        OperatorLevel other = (OperatorLevel) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Operatorlevel[ id=" + id + " ]";
    }

}

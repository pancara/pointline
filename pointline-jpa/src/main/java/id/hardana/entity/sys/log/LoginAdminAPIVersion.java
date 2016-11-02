/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.log;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Trisna
 */
@Entity
@Table(name = "loginadminapiversion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LoginAdminAPIVersion.findAll", query = "SELECT l FROM LoginAdminAPIVersion l"),
    @NamedQuery(name = "LoginAdminAPIVersion.findById", query = "SELECT l FROM LoginAdminAPIVersion l WHERE l.id = :id"),
    @NamedQuery(name = "LoginAdminAPIVersion.findByLoginAdminId", query = "SELECT l FROM LoginAdminAPIVersion l WHERE l.loginAdminId = :loginAdminId")})
public class LoginAdminAPIVersion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "loginadminid")
    private Long loginAdminId;
    @Column(name = "apiversion")
    private Integer apiVersion;

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginAdminId() {
        return loginAdminId == null ? null : String.valueOf(loginAdminId);
    }

    public void setLoginAdminId(Long loginAdminId) {
        this.loginAdminId = loginAdminId;
    }

    public String getApiVersion() {
        return apiVersion == null ? null : String.valueOf(apiVersion);
    }

    public void setApiVersion(Integer apiVersion) {
        this.apiVersion = apiVersion;
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
        if (!(object instanceof LoginAdminAPIVersion)) {
            return false;
        }
        LoginAdminAPIVersion other = (LoginAdminAPIVersion) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "id.hardana.entity.api.LoginAdminAPIVersion[ id=" + id + " ]";
    }

}

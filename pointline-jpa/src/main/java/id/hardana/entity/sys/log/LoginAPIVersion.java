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
@Table(name = "loginapiversion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LoginAPIVersion.findAll", query = "SELECT l FROM LoginAPIVersion l"),
    @NamedQuery(name = "LoginAPIVersion.findById", query = "SELECT l FROM LoginAPIVersion l WHERE l.id = :id"),
    @NamedQuery(name = "LoginAPIVersion.findByLoginId", query = "SELECT l FROM LoginAPIVersion l WHERE l.loginId = :loginId")})
public class LoginAPIVersion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "loginid")
    private Long loginId;
    @Column(name = "apiversion")
    private Integer apiVersion;

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId == null ? null : String.valueOf(loginId);
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
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
        if (!(object instanceof LoginAPIVersion)) {
            return false;
        }
        LoginAPIVersion other = (LoginAPIVersion) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "id.hardana.entity.api.LoginAPIVersion[ id=" + id + " ]";
    }

}

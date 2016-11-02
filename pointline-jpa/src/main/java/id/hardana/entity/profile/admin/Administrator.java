/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.admin;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Trisna
 */
@Entity
@Table(name = "administrator")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Administrator.findAll", query = "SELECT a FROM Administrator a"),
        @NamedQuery(name = "Administrator.findById", query = "SELECT a FROM Administrator a WHERE a.id = :id"),
        @NamedQuery(name = "Personal.findByUserName", query = "SELECT a FROM Administrator a WHERE a.userName = :userName")})
public class Administrator implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "username", unique = true)
    private String userName;
    @Column(name = "password1")
    private String password1;
    @Column(name = "password2")
    private String password2;
    @Column(name = "password3")
    private String password3;
    @Column(name = "passwordexpired")
    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordExpired;

    public Administrator() {
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

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getPassword3() {
        return password3;
    }

    public void setPassword3(String password3) {
        this.password3 = password3;
    }

    public String getPasswordExpired() {
        return passwordExpired == null ? null : DATE_FORMAT.format(passwordExpired);
    }

    public void setPasswordExpired(Date passwordExpired) {
        this.passwordExpired = passwordExpired;
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
        if (!(object instanceof Administrator)) {
            return false;
        }
        Administrator other = (Administrator) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Administrator[ id=" + id + " ]";
    }

}

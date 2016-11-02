/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package id.hardana.entity.sys.log;

import id.hardana.entity.sys.enums.LoginStatusEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Trisna
 */
@Entity
@Table(name = "login")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Login.findAll", query = "SELECT l FROM Login l"),
        @NamedQuery(name = "Login.findById", query = "SELECT l FROM Login l WHERE l.id = :id"),
        @NamedQuery(name = "Login.findActiveUserMerchantByChannel", query = "SELECT l FROM Login l WHERE l.channelId = :channelId AND l.merchantId = :merchantId AND l.userId = :userId AND l.status = :status"),
        @NamedQuery(name = "Login.findActiveUserByChannel", query = "SELECT l FROM Login l WHERE l.channelId = :channelId AND l.merchantId is null AND l.userId = :userId AND l.status = :status")})
public class Login implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "channelid")
    private Long channelId;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "userid")
    private Long userId;
    @Column(name = "logintime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginTime;
    @Column(name = "logouttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logoutTime;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private LoginStatusEnum status;
    @Column(name = "sessionid")
    private String sessionId;
    @Column(name = "sessionexpired")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sessionExpired;
    @Column(name = "tokenold")
    private String tokenOld;
    @Column(name = "tokenoldexpired")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenOldExpired;
    @Column(name = "tokennew")
    private String tokenNew;
    @Column(name = "tokennewexpired")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenNewExpired;

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId == null ? null : String.valueOf(channelId);
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getUserId() {
        return userId == null ? null : String.valueOf(userId);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginTime() {
        return loginTime == null ? null : DATE_FORMAT.format(loginTime);
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLogoutTime() {
        return logoutTime == null ? null : DATE_FORMAT.format(logoutTime);
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public LoginStatusEnum getStatus() {
        return status;
    }

    public void setStatus(LoginStatusEnum status) {
        this.status = status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionExpired() {
        return DATE_FORMAT.format(sessionExpired);
    }

    public void setSessionExpired(Date sessionExpired) {
        this.sessionExpired = sessionExpired;
    }

    public String getTokenOld() {
        return tokenOld;
    }

    public void setTokenOld(String tokenOld) {
        this.tokenOld = tokenOld;
    }

    public String getTokenOldExpired() {
        return tokenOldExpired == null ? null : DATE_FORMAT.format(tokenOldExpired);
    }

    public void setTokenOldExpired(Date tokenOldExpired) {
        this.tokenOldExpired = tokenOldExpired;
    }

    public String getTokenNew() {
        return tokenNew;
    }

    public void setTokenNew(String tokenNew) {
        this.tokenNew = tokenNew;
    }

    public String getTokenNewExpired() {
        return tokenNewExpired == null ? null : DATE_FORMAT.format(tokenNewExpired);
    }

    public void setTokenNewExpired(Date tokenNewExpired) {
        this.tokenNewExpired = tokenNewExpired;
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
        if (!(object instanceof Login)) {
            return false;
        }
        Login other = (Login) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "id.hardana.entity.api.Login[ id=" + id + " ]";
    }

}

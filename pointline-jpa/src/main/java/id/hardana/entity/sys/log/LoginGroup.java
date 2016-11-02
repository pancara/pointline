/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package id.hardana.entity.sys.log;

import id.hardana.entity.sys.enums.LoginGroupStatusEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Arya
 */
@Entity
@Table(name = "logingroup")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "LoginGroup.findAll", query = "SELECT lg FROM LoginGroup lg"),
        @NamedQuery(name = "LoginGroup.findById", query = "SELECT lg FROM LoginGroup lg WHERE lg.id = :id"),
        @NamedQuery(name = "LoginGroup.findActiveUserGroupByChannel", query = "SELECT lg FROM LoginGroup lg WHERE lg.channelId = :channelId AND lg.groupId = :groupId AND lg.userId = :userId AND lg.status = :status"),
        @NamedQuery(name = "LoginGroup.findActiveUserByChannel", query = "SELECT lg FROM LoginGroup lg WHERE lg.channelId = :channelId AND lg.groupId is null AND lg.userId = :userId AND lg.status = :status")})

public class LoginGroup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "channelid")
    private Long channelId;
    @Column(name = "groupid")
    private Long groupId;
    @Column(name = "userid")
    private Long userId;
    @Column(name = "loginGrouptime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginGroupTime;
    @Column(name = "logouttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logoutTime;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private LoginGroupStatusEnum status;
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

    public String getGroupId() {
        return groupId == null ? null : String.valueOf(groupId);
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId == null ? null : String.valueOf(userId);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginGroupTime() {
        return loginGroupTime == null ? null : DATE_FORMAT.format(loginGroupTime);
    }

    public void setLoginGroupTime(Date loginGroupTime) {
        this.loginGroupTime = loginGroupTime;
    }

    public String getLogoutTime() {
        return logoutTime == null ? null : DATE_FORMAT.format(logoutTime);
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public LoginGroupStatusEnum getStatus() {
        return status;
    }

    public void setStatus(LoginGroupStatusEnum status) {
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
        if (!(object instanceof LoginGroup)) {
            return false;
        }
        LoginGroup other = (LoginGroup) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "id.hardana.entity.api.LoginGroup[ id=" + id + " ]";
    }

    public LoginGroup get(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.channel;

 import id.hardana.entity.sys.enums.ApplicationTypeEnum;
 import id.hardana.entity.sys.enums.ChannelStatusEnum;

 import javax.persistence.*;
 import javax.xml.bind.annotation.XmlRootElement;
 import java.io.Serializable;

 /**
  *
  * @author Trisna
  */
 @Entity
 @Table(name = "channelapi")
 @XmlRootElement
 @NamedQueries({
     @NamedQuery(name = "ChannelAPI.findAll", query = "SELECT c FROM ChannelAPI c"),
     @NamedQuery(name = "ChannelAPI.findById", query = "SELECT c FROM ChannelAPI c WHERE c.id = :id"),
     @NamedQuery(name = "ChannelAPI.findByApplicationKey", query = "SELECT c FROM ChannelAPI c WHERE c.applicationKey = :applicationKey")})
 public class ChannelAPI implements Serializable {

     private static final long serialVersionUID = 1L;
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Long id;
     @Column(name = "applicationname", unique = true)
     private String applicationName;
     @Column(name = "applicationkey", unique = true)
     private String applicationKey;
     @Column(name = "applicationsecret")
     private String applicationSecret;
     @Column(name = "applicationversion")
     private String applicationVersion;
     @Lob
     @Column(name = "applicationdescription")
     private String applicationDescription;
     @Column(name = "applicationweb")
     private String applicationWeb;
     @Lob
     @Column(name = "applicationlogo")
     private String applicationLogo;
     @Lob
     @Column(name = "applicationimage")
     private String applicationImage;
     @Enumerated(EnumType.ORDINAL)
     @Column(name = "applicationtype")
     private ApplicationTypeEnum applicationType;
     @Column(name = "developername")
     private String developerName;
     @Column(name = "developerphone")
     private String developerPhone;
     @Column(name = "developeremail")
     private String developerEmail;
     @Column(name = "developerweb")
     private String developerWeb;
     @Column(name = "islocalchannel")
     private Boolean isLocalChannel;
     @Enumerated(EnumType.ORDINAL)
     @Column(name = "status")
     private ChannelStatusEnum status;

     public String getId() {
         return id == null ? null : String.valueOf(id);
     }

     public void setId(Long id) {
         this.id = id;
     }

     public String getApplicationName() {
         return applicationName;
     }

     public void setApplicationName(String applicationName) {
         this.applicationName = applicationName;
     }

     public String getApplicationKey() {
         return applicationKey;
     }

     public void setApplicationKey(String applicationKey) {
         this.applicationKey = applicationKey;
     }

     public String getApplicationSecret() {
         return applicationSecret;
     }

     public void setApplicationSecret(String applicationSecret) {
         this.applicationSecret = applicationSecret;
     }

     public String getApplicationVersion() {
         return applicationVersion;
     }

     public void setApplicationVersion(String applicationVersion) {
         this.applicationVersion = applicationVersion;
     }

     public String getApplicationDescription() {
         return applicationDescription;
     }

     public void setApplicationDescription(String applicationDescription) {
         this.applicationDescription = applicationDescription;
     }

     public String getApplicationWeb() {
         return applicationWeb;
     }

     public void setApplicationWeb(String applicationWeb) {
         this.applicationWeb = applicationWeb;
     }

     public String getApplicationLogo() {
         return applicationLogo;
     }

     public void setApplicationLogo(String applicationLogo) {
         this.applicationLogo = applicationLogo;
     }

     public String getApplicationImage() {
         return applicationImage;
     }

     public void setApplicationImage(String applicationImage) {
         this.applicationImage = applicationImage;
     }

     public ApplicationTypeEnum getApplicationType() {
         return applicationType;
     }

     public void setApplicationType(ApplicationTypeEnum applicationType) {
         this.applicationType = applicationType;
     }

     public String getDeveloperName() {
         return developerName;
     }

     public void setDeveloperName(String developerName) {
         this.developerName = developerName;
     }

     public String getDeveloperPhone() {
         return developerPhone;
     }

     public void setDeveloperPhone(String developerPhone) {
         this.developerPhone = developerPhone;
     }

     public String getDeveloperEmail() {
         return developerEmail;
     }

     public void setDeveloperEmail(String developerEmail) {
         this.developerEmail = developerEmail;
     }

     public String getDeveloperWeb() {
         return developerWeb;
     }

     public void setDeveloperWeb(String developerWeb) {
         this.developerWeb = developerWeb;
     }

     public String getIsLocalChannel() {
         return isLocalChannel == null ? null : String.valueOf(isLocalChannel);
     }

     public void setIsLocalChannel(Boolean isLocalChannel) {
         this.isLocalChannel = isLocalChannel;
     }

     public ChannelStatusEnum getStatus() {
         return status;
     }

     public void setStatus(ChannelStatusEnum status) {
         this.status = status;
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
         if (!(object instanceof ChannelAPI)) {
             return false;
         }
         ChannelAPI other = (ChannelAPI) object;
         return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
     }

     @Override
     public String toString() {
         return "id.hardana.entity.api.ChannelAPI[ id=" + id + " ]";
     }

 }

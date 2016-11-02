 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.channel;

 import javax.persistence.*;
 import javax.xml.bind.annotation.XmlRootElement;
 import java.io.Serializable;

 /**
  *
  * @author Trisna
  */
 @Entity
 @Table(name = "channelmoduleaccessor")
 @XmlRootElement
 public class ChannelModuleAccessor implements Serializable {

     private static final long serialVersionUID = 1L;
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Long id;
     @Column(name = "channelid")
     private Long channelId;
     @Column(name = "moduleid")
     private Long moduleId;

     public String getId() {
         return id == null ? null : String.valueOf(id);
     }

     public void setId(Long id) {
         this.id = id;
     }

     public String getChannelId() {
         return String.valueOf(channelId);
     }

     public void setChannelId(Long channelId) {
         this.channelId = channelId;
     }

     public String getModuleId() {
         return moduleId == null ? null : String.valueOf(moduleId);
     }

     public void setModuleId(Long moduleId) {
         this.moduleId = moduleId;
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
         if (!(object instanceof ChannelModuleAccessor)) {
             return false;
         }
         ChannelModuleAccessor other = (ChannelModuleAccessor) object;
         return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
     }

     @Override
     public String toString() {
         return "id.hardana.entity.api.ChannelModuleAccessor[ id=" + id + " ]";
     }

 }

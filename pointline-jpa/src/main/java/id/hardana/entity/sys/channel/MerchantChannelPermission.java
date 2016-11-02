 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.channel;

 import id.hardana.entity.sys.enums.ChannelPermissionStatusEnum;

 import javax.persistence.*;
 import javax.xml.bind.annotation.XmlRootElement;
 import java.io.Serializable;

 /**
  *
  * @author Trisna
  */
 @Entity
 @Table(name = "merchantchannelpermission")
 @XmlRootElement
 public class MerchantChannelPermission implements Serializable {

     private static final long serialVersionUID = 1L;
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Long id;
     @Column(name = "channelid")
     private Long channelId;
     @Column(name = "merchantid")
     private Long merchantId;
     @Enumerated(EnumType.ORDINAL)
     @Column(name = "status")
     private ChannelPermissionStatusEnum status;

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

     public ChannelPermissionStatusEnum getStatus() {
         return status;
     }

     public void setStatus(ChannelPermissionStatusEnum status) {
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
         if (!(object instanceof MerchantChannelPermission)) {
             return false;
         }
         MerchantChannelPermission other = (MerchantChannelPermission) object;
         return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
     }

     @Override
     public String toString() {
         return "id.hardana.entity.api.MerchantChannelPermission[ id=" + id + " ]";
     }

 }

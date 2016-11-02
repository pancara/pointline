 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.log;

 import id.hardana.entity.sys.enums.ResponseStatusEnum;

 import javax.persistence.*;
 import javax.xml.bind.annotation.XmlRootElement;
 import java.io.Serializable;
 import java.text.SimpleDateFormat;
 import java.util.Date;

 /**
  *
  * @author Trisna
  */
 @Entity
 @Table(name = "logindetail")
 @XmlRootElement
 public class LoginDetail implements Serializable {

     private static final long serialVersionUID = 1L;
     @Transient
     private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Long id;
     @Column(name = "loginid")
     private Long loginId;
     @Column(name = "methodid")
     private Long methodId;
     @Column(name = "requesttime")
     @Temporal(TemporalType.TIMESTAMP)
     private Date requestTime;
     @Enumerated(EnumType.ORDINAL)
     @Column(name = "responsestatus")
     private ResponseStatusEnum responseStatus;

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

     public String getMethodId() {
         return methodId == null ? null : String.valueOf(methodId);
     }

     public void setMethodId(Long methodId) {
         this.methodId = methodId;
     }

     public String getRequestTime() {
         return requestTime == null ? null : DATE_FORMAT.format(requestTime);
     }

     public void setRequestTime(Date requestTime) {
         this.requestTime = requestTime;
     }

     public ResponseStatusEnum getResponseStatus() {
         return responseStatus;
     }

     public void setResponseStatus(ResponseStatusEnum responseStatus) {
         this.responseStatus = responseStatus;
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
         if (!(object instanceof LoginDetail)) {
             return false;
         }
         LoginDetail other = (LoginDetail) object;
         return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
     }

     @Override
     public String toString() {
         return "id.hardana.entity.api.Login[ id=" + id + " ]";
     }

 }

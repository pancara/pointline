 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.api;

 import javax.persistence.*;
 import javax.xml.bind.annotation.XmlRootElement;
 import java.io.Serializable;

 /**
  *
  * @author Trisna
  */
 @Entity
 @Table(name = "apimethod")
 @XmlRootElement
 public class APIMethod implements Serializable {

     private static final long serialVersionUID = 1L;
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Long id;
     @Column(name = "methodname", unique = true)
     private String methodName;
     @Column(name = "moduleid")
     private Long moduleId;
     @Lob
     @Column(name = "description")
     private String description;
     @Column(name = "methodnumber")
     private Integer methodNumber;

     public String getId() {
         return id == null ? null : String.valueOf(id);
     }

     public void setId(Long id) {
         this.id = id;
     }

     public String getMethodName() {
         return methodName;
     }

     public void setMethodName(String methodName) {
         this.methodName = methodName;
     }

     public String getModuleId() {
         return moduleId == null ? null : String.valueOf(moduleId);
     }

     public void setModuleId(Long moduleId) {
         this.moduleId = moduleId;
     }

     public String getDescription() {
         return description;
     }

     public void setDescription(String description) {
         this.description = description;
     }

     public String getMethodNumber() {
         return methodNumber == null ? null : String.valueOf(methodNumber);
     }

     public void setMethodNumber(Integer methodNumber) {
         this.methodNumber = methodNumber;
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
         if (!(object instanceof APIMethod)) {
             return false;
         }
         APIMethod other = (APIMethod) object;
         return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
     }

     @Override
     public String toString() {
         return "id.hardana.entity.api.APIMethod[ id=" + id + " ]";
     }

 }

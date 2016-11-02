 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.api;

 import id.hardana.entity.sys.enums.ApplicationTypeEnum;

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
 @Table(name = "apimodule")
 @XmlRootElement
 public class APIModule implements Serializable {

     private static final long serialVersionUID = 1L;
     @Transient
     private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Long id;
     @Column(name = "name", unique = true)
     private String name;
     @Lob
     @Column(name = "description")
     private String description;
     @Column(name = "version")
     private String version;
     @Column(name = "releasedate")
     @Temporal(TemporalType.TIMESTAMP)
     private Date releaseDate;
     @Column(name = "numberofmethod")
     private Integer numberOfMethod;
     @Enumerated(EnumType.ORDINAL)
     @Column(name = "applicationtype")
     private ApplicationTypeEnum applicationType;

     public String getId() {
         return id == null ? null : String.valueOf(id);
     }

     public void setId(Long id) {
         this.id = id;
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public String getDescription() {
         return description;
     }

     public void setDescription(String description) {
         this.description = description;
     }

     public String getVersion() {
         return version;
     }

     public void setVersion(String version) {
         this.version = version;
     }

     public String getReleaseDate() {
         return releaseDate == null ? null : DATE_FORMAT.format(releaseDate);
     }

     public void setReleaseDate(Date releaseDate) {
         this.releaseDate = releaseDate;
     }

     public String getNumberOfMethod() {
         return numberOfMethod == null ? null : String.valueOf(numberOfMethod);
     }

     public void setNumberOfMethod(Integer numberOfMethod) {
         this.numberOfMethod = numberOfMethod;
     }

     public ApplicationTypeEnum getApplicationType() {
         return applicationType;
     }

     public void setApplicationType(ApplicationTypeEnum applicationType) {
         this.applicationType = applicationType;
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
         if (!(object instanceof APIModule)) {
             return false;
         }
         APIModule other = (APIModule) object;
         return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
     }

     @Override
     public String toString() {
         return "id.hardana.entity.api.APIModule[ id=" + id + " ]";
     }

 }

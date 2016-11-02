/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.merchant;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Trisna
 */
@Entity
@Table(name = "operatorlevelmoduleaccessor")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "OperatorLevelModuleAccessor.findAll", query = "SELECT o FROM OperatorLevelModuleAccessor o"),
        @NamedQuery(name = "OperatorLevelModuleAccessor.findById", query = "SELECT o FROM OperatorLevelModuleAccessor o WHERE o.id = :id"),
        @NamedQuery(name = "OperatorLevelModuleAccessor.findByOperatorLevelId", query = "SELECT o FROM OperatorLevelModuleAccessor o WHERE o.operatorLevelId = :opLevelId")})
public class OperatorLevelModuleAccessor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "operatorlevelid")
    private Long operatorLevelId;
    @Column(name = "moduleid")
    private Long moduleId;

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperatorLevelId() {
        return operatorLevelId == null ? null : String.valueOf(operatorLevelId);
    }

    public void setOperatorLevelId(Long operatorLevelId) {
        this.operatorLevelId = operatorLevelId;
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
        if (!(object instanceof OperatorLevelModuleAccessor)) {
            return false;
        }
        OperatorLevelModuleAccessor other = (OperatorLevelModuleAccessor) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "id.hardana.entity.api.OperatorLevelModuleAccessor[ id=" + id + " ]";
    }

}

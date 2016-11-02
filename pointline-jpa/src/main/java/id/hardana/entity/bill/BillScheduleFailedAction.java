/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Trisna
 */
@Entity
@Table(name = "billschedulefailedaction")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BillScheduleFailedAction.findAll", query = "SELECT b FROM BillScheduleFailedAction b"),
    @NamedQuery(name = "BillScheduleFailedAction.findById", query = "SELECT b FROM BillScheduleFailedAction b WHERE b.id = :id")})
public class BillScheduleFailedAction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "failedaction", unique = true)
    private String failedAction;

    public BillScheduleFailedAction() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFailedAction() {
        return failedAction;
    }

    public void setFailedAction(String failedAction) {
        this.failedAction = failedAction;
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
        if (!(object instanceof BillScheduleFailedAction)) {
            return false;
        }
        BillScheduleFailedAction other = (BillScheduleFailedAction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.bill.BillScheduleFailedAction[ id=" + id + " ]";
    }

}

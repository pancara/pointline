/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author Trisna
 */
@Entity
@Table(name = "transactiontransferpersonaltocardtype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionTransferPersonalToCardType.findAll", query = "SELECT t FROM TransactionTransferPersonalToCardType t"),
    @NamedQuery(name = "TransactionTransferPersonalToCardType.findById", query = "SELECT t FROM TransactionTransferPersonalToCardType t WHERE t.id = :id")})
public class TransactionTransferPersonalToCardType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "type", unique = true)
    private String type;

    public TransactionTransferPersonalToCardType() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        if (!(object instanceof TransactionTransferPersonalToCardType)) {
            return false;
        }
        TransactionTransferPersonalToCardType other = (TransactionTransferPersonalToCardType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionTransferPersonalToCardType[ id=" + id + " ]";
    }

}

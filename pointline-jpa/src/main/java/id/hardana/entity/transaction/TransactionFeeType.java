/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "transactionfeetype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionFeeType.findAll", query = "SELECT t FROM TransactionFeeType t"),
    @NamedQuery(name = "TransactionFeeType.findById", query = "SELECT t FROM TransactionFeeType t WHERE t.id = :id")})
public class TransactionFeeType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "type", unique = true)
    private String type;
    @Column(name = "fee")
    private BigDecimal fee;

    public TransactionFeeType() {
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

    public String getFee() {
        return fee == null ? null : fee.toPlainString();
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
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
        if (!(object instanceof TransactionFeeType)) {
            return false;
        }
        TransactionFeeType other = (TransactionFeeType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.TransactionFeeType[ id=" + id + " ]";
    }

}

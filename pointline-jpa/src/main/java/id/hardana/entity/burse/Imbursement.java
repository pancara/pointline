/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.burse;

import id.hardana.entity.burse.enums.BurseStatusEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hanafi
 */
@Entity
@Table(name = "imbursement")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Imbursement.findAll", query = "SELECT i FROM Imbursement i"),
        @NamedQuery(name = "Imbursement.findById", query = "SELECT i FROM Imbursement i WHERE i.id = :id")})
public class Imbursement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "number", unique = true)
    private String number;

    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private BurseStatusEnum status;

    @Column(name = "merchantid")
    private Long merchantId;


    public Imbursement() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateTime() {
        return dateTime == null ? null : DATE_FORMAT.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getAmount() {
        return amount == null ? null : amount.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BurseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BurseStatusEnum status) {
        this.status = status;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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
        if (!(object instanceof Imbursement)) {
            return false;
        }
        Imbursement other = (Imbursement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.transaction.Imbursement[ id=" + id + " ]";
    }

}

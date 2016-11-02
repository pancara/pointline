/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill;

import id.hardana.entity.bill.enums.BillTypeEnum;

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
@Table(name = "billuid")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BillUID.findAll", query = "SELECT b FROM BillUID b"),
    @NamedQuery(name = "BillUID.findById", query = "SELECT b FROM BillUID b WHERE b.id = :id")})
public class BillUID implements Serializable {

    private static final long serialVersionUID = 1L;
    @Version
    private long version;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private BillTypeEnum type;
    @Column(name = "billname")
    private String billName;
    @Column(name = "creatorid")
    private Long creatorId;
    @Column(name = "outletid")
    private Long outletId;
    @Column(name = "operatorid")
    private Long operatorId;
    @Column(name = "uniqueid")
    private String uniqueId;
    @Column(name = "generateddate")
    @Temporal(TemporalType.DATE)
    private Date generatedDate;
    @Column(name = "payerids")
    private String payerIds;
    @Column(name = "isused")
    private Boolean isUsed;
    @Column(name = "billids")
    private String billIds;

    public BillUID() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BillTypeEnum getType() {
        return type;
    }

    public void setType(BillTypeEnum type) {
        this.type = type;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getCreatorId() {
        return creatorId == null ? null : String.valueOf(creatorId);
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getOutletId() {
        return outletId == null ? null : String.valueOf(outletId);
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getOperatorId() {
        return operatorId == null ? null : String.valueOf(operatorId);
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getGeneratedDate() {
        return generatedDate == null ? null : DATE_FORMAT.format(generatedDate);
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getIsUsed() {
        return isUsed == null ? null : String.valueOf(isUsed);
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public String getBillIds() {
        return billIds;
    }

    public void setBillIds(String billIds) {
        this.billIds = billIds;
    }

    public String getPayerIds() {
        return payerIds;
    }

    public void setPayerIds(String payerIds) {
        this.payerIds = payerIds;
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
        if (!(object instanceof BillUID)) {
            return false;
        }
        BillUID other = (BillUID) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.invoice.BillUID[ id=" + id + " ]";
    }

}

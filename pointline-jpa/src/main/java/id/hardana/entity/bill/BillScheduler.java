/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill;

import id.hardana.entity.bill.enums.BillScheduleFailedActionEnum;
import id.hardana.entity.bill.enums.BillScheduleStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Trisna
 */
@Entity
@Table(name = "billsceduler")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BillScheduler.findAll", query = "SELECT b FROM BillScheduler b"),
    @NamedQuery(name = "BillScheduler.findById", query = "SELECT b FROM BillScheduler b WHERE b.id = :id"),
    @NamedQuery(name = "BillScheduler.findByBillId", query = "SELECT b FROM BillScheduler b WHERE b.billId = :billId")})
public class BillScheduler implements Serializable {

    private static final long serialVersionUID = 1L;
    @Version
    private long version;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "billid")
    private Long billId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "executiondate")
    private Date executionDate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "lastexecutionstatus")
    private ResponseStatusEnum lastExecutionStatus;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "schedulestatus")
    private BillScheduleStatusEnum scheduleStatus;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "failedaction")
    private BillScheduleFailedActionEnum failedAction;
    @Column(name = "maxtrial")
    private Long maxTrial;
    @Column(name = "trialnumber")
    private Long trialNumber;

    public BillScheduler() {
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

    public String getBillId() {
        return billId == null ? null : String.valueOf(billId);
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getExecutionDate() {
        return executionDate == null ? null : DATE_FORMAT.format(executionDate);
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public ResponseStatusEnum getLastExecutionStatus() {
        return lastExecutionStatus;
    }

    public void setLastExecutionStatus(ResponseStatusEnum lastExecutionStatus) {
        this.lastExecutionStatus = lastExecutionStatus;
    }

    public BillScheduleStatusEnum getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(BillScheduleStatusEnum scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public BillScheduleFailedActionEnum getFailedAction() {
        return failedAction;
    }

    public void setFailedAction(BillScheduleFailedActionEnum failedAction) {
        this.failedAction = failedAction;
    }

    public String getMaxTrial() {
        return maxTrial == null ? null : String.valueOf(maxTrial);
    }

    public void setMaxTrial(Long maxTrial) {
        this.maxTrial = maxTrial;
    }

    public String getTrialNumber() {
        return trialNumber == null ? null : String.valueOf(trialNumber);
    }

    public void setTrialNumber(Long trialNumber) {
        this.trialNumber = trialNumber;
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
        if (!(object instanceof BillScheduler)) {
            return false;
        }
        BillScheduler other = (BillScheduler) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.bill.BillScheduler[ id=" + id + " ]";
    }

}

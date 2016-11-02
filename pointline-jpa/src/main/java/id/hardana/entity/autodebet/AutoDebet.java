/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.autodebet;

import id.hardana.entity.autodebet.enums.AutoDebetFailedActionEnum;
import id.hardana.entity.autodebet.enums.AutodebetFrequencyTypeEnum;
import id.hardana.entity.autodebet.enums.AutodebetStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Trisna
 */
@Entity
@Table(name = "autodebet")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutoDebet.findAll", query = "SELECT a FROM AutoDebet a"),
    @NamedQuery(name = "AutoDebet.findById", query = "SELECT a FROM AutoDebet a WHERE a.id = :id"),
    @NamedQuery(name = "AutoDebet.findByNumber", query = "SELECT a FROM AutoDebet a WHERE a.number = :number"),
    @NamedQuery(name = "AutoDebet.findByNumberAndSenderId", query = "SELECT a FROM AutoDebet a WHERE a.number = :number AND a.fromId = :fromId")})
public class AutoDebet implements Serializable {

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
    @Column(name = "number", unique = true)
    private String number;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private TransactionTransferTypeEnum type;
    @Column(name = "fromid")
    private Long fromId;
    @Column(name = "toid")
    private Long toId;
    @Column(name = "amount")
    private BigDecimal amount;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private AutodebetStatusEnum status;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createddate")
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "closeddate")
    private Date closedDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "executiondate")
    private Date executionDate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "lastexecutionstatus")
    private ResponseStatusEnum lastExecutionStatus;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "failedaction")
    private AutoDebetFailedActionEnum failedAction;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "frequencytype")
    private AutodebetFrequencyTypeEnum frequencyType;
    @Column(name = "schedulefrequency")
    private Long scheduleFrequency;
    @Column(name = "schedulenumber")
    private Long scheduleNumber;
    @Column(name = "successexecutenumber")
    private Long successExecuteNumber;
    @Column(name = "failedexecutenumber")
    private Long failedExecuteNumber;

    public AutoDebet() {
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public TransactionTransferTypeEnum getType() {
        return type;
    }

    public void setType(TransactionTransferTypeEnum type) {
        this.type = type;
    }

    public String getFromId() {
        return fromId == null ? null : String.valueOf(fromId);
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId == null ? null : String.valueOf(toId);
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public String getAmount() {
        return amount == null ? null : amount.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public AutodebetStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AutodebetStatusEnum status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate == null ? null : DATE_FORMAT.format(createdDate);
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getClosedDate() {
        return closedDate == null ? null : DATE_FORMAT.format(closedDate);
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
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

    public AutoDebetFailedActionEnum getFailedAction() {
        return failedAction;
    }

    public void setFailedAction(AutoDebetFailedActionEnum failedAction) {
        this.failedAction = failedAction;
    }

    public AutodebetFrequencyTypeEnum getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(AutodebetFrequencyTypeEnum frequencyType) {
        this.frequencyType = frequencyType;
    }

    public String getScheduleFrequency() {
        return scheduleFrequency == null ? null : String.valueOf(scheduleFrequency);
    }

    public void setScheduleFrequency(Long scheduleFrequency) {
        this.scheduleFrequency = scheduleFrequency;
    }

    public String getScheduleNumber() {
        return scheduleNumber == null ? null : String.valueOf(scheduleNumber);
    }

    public void setScheduleNumber(Long scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public String getSuccessExecuteNumber() {
        return successExecuteNumber == null ? null : String.valueOf(successExecuteNumber);
    }

    public void setSuccessExecuteNumber(Long successExecuteNumber) {
        this.successExecuteNumber = successExecuteNumber;
    }

    public String getFailedExecuteNumber() {
        return failedExecuteNumber == null ? null : String.valueOf(failedExecuteNumber);
    }

    public void setFailedExecuteNumber(Long failedExecuteNumber) {
        this.failedExecuteNumber = failedExecuteNumber;
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
        if (!(object instanceof AutoDebet)) {
            return false;
        }
        AutoDebet other = (AutoDebet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.autodebet.AutoDebet[ id=" + id + " ]";
    }

}

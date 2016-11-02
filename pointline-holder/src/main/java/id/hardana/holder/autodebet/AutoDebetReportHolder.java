/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.autodebet;

import id.hardana.entity.autodebet.enums.AutoDebetFailedActionEnum;
import id.hardana.entity.autodebet.enums.AutodebetFrequencyTypeEnum;
import id.hardana.entity.autodebet.enums.AutodebetStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class AutoDebetReportHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String autoDebetNumber;
    private TransactionTransferTypeEnum transferType;
    private BigDecimal amount;
    private AutodebetStatusEnum status;
    private Date createdDate;
    private Date closedDate;
    private Date nextExecutionDate;
    private ResponseStatusEnum lastExecutionStatus;
    private AutoDebetFailedActionEnum failedAction;
    private AutodebetFrequencyTypeEnum frequencyType;
    private Long scheduleFrequency;
    private Long scheduleNumber;
    private Long successExecuteNumber;
    private Long failedExecuteNumber;
//    Transfer To Personal
    private String destinationAccount;
    private String destinationFirstName;
    private String destinationLastName;
//    Transfer To Card
    private String destionationCardPAN;
    private String destionationCardName;

    public AutoDebetReportHolder(String autoDebetNumber, TransactionTransferTypeEnum transferType,
            BigDecimal amount, AutodebetStatusEnum status, Date createdDate, Date closedDate,
            Date nextExecutionDate, ResponseStatusEnum lastExecutionStatus,
            AutoDebetFailedActionEnum failedAction, AutodebetFrequencyTypeEnum frequencyType,
            Long scheduleFrequency, Long scheduleNumber, Long successExecuteNumber, Long failedExecuteNumber,
            String destinationAccount, String destinationFirstName, String destinationLastName,
            String destionationCardPAN, String destionationCardName) {
        this.autoDebetNumber = autoDebetNumber;
        this.transferType = transferType;
        this.amount = amount;
        this.status = status;
        this.createdDate = createdDate;
        this.closedDate = closedDate;
        this.nextExecutionDate = nextExecutionDate;
        this.lastExecutionStatus = lastExecutionStatus;
        this.failedAction = failedAction;
        this.frequencyType = frequencyType;
        this.scheduleFrequency = scheduleFrequency;
        this.scheduleNumber = scheduleNumber;
        this.successExecuteNumber = successExecuteNumber;
        this.failedExecuteNumber = failedExecuteNumber;
        this.destinationAccount = destinationAccount;
        this.destinationFirstName = destinationFirstName;
        this.destinationLastName = destinationLastName;
        this.destionationCardPAN = destionationCardPAN;
        this.destionationCardName = destionationCardName;
    }

    public String getAutoDebetNumber() {
        return autoDebetNumber;
    }

    public void setAutoDebetNumber(String autoDebetNumber) {
        this.autoDebetNumber = autoDebetNumber;
    }

    public TransactionTransferTypeEnum getTransferType() {
        return transferType;
    }

    public void setTransferType(TransactionTransferTypeEnum transferType) {
        this.transferType = transferType;
    }

    public String getAmount() {
        return amount.toPlainString();
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

    public String getNextExecutionDate() {
        return nextExecutionDate == null ? null : DATE_FORMAT.format(nextExecutionDate);
    }

    public void setNextExecutionDate(Date nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
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

    public Long getScheduleFrequency() {
        return scheduleFrequency;
    }

    public void setScheduleFrequency(Long scheduleFrequency) {
        this.scheduleFrequency = scheduleFrequency;
    }

    public Long getScheduleNumber() {
        return scheduleNumber;
    }

    public void setScheduleNumber(Long scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public Long getSuccessExecuteNumber() {
        return successExecuteNumber;
    }

    public void setSuccessExecuteNumber(Long successExecuteNumber) {
        this.successExecuteNumber = successExecuteNumber;
    }

    public Long getFailedExecuteNumber() {
        return failedExecuteNumber;
    }

    public void setFailedExecuteNumber(Long failedExecuteNumber) {
        this.failedExecuteNumber = failedExecuteNumber;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public String getDestinationFirstName() {
        return destinationFirstName;
    }

    public void setDestinationFirstName(String destinationFirstName) {
        this.destinationFirstName = destinationFirstName;
    }

    public String getDestinationLastName() {
        return destinationLastName;
    }

    public void setDestinationLastName(String destinationLastName) {
        this.destinationLastName = destinationLastName;
    }

    public String getDestionationCardPAN() {
        return destionationCardPAN;
    }

    public void setDestionationCardPAN(String destionationCardPAN) {
        this.destionationCardPAN = destionationCardPAN;
    }

    public String getDestionationCardName() {
        return destionationCardName;
    }

    public void setDestionationCardName(String destionationCardName) {
        this.destionationCardName = destionationCardName;
    }

}

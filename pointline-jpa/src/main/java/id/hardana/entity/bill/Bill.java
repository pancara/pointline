/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill;

import id.hardana.entity.bill.enums.*;

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
@Table(name = "bill")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bill.findAll", query = "SELECT b FROM Bill b"),
    @NamedQuery(name = "Bill.findById", query = "SELECT b FROM Bill b WHERE b.id = :id"),
    @NamedQuery(name = "Bill.findByNumber", query = "SELECT b FROM Bill b WHERE b.number = :number"),
    @NamedQuery(name = "Bill.findByInvoiceId", query = "SELECT b FROM Bill b WHERE b.invoiceId = :invoiceId"),
    @NamedQuery(name = "Bill.findByTransactionId", query = "SELECT b FROM Bill b WHERE b.transactionId = :transactionId"),
    @NamedQuery(name = "Bill.findByMerchantIdAndNumber", query = "SELECT b FROM Bill b WHERE b.type = id.hardana.entity.bill.enums.BillTypeEnum.MERCHANT_BILL AND b.creatorId = :merchantId AND b.number = :number"),
    @NamedQuery(name = "Bill.findByPersonalCreatorIdAndNumber", query = "SELECT b FROM Bill b WHERE b.type = id.hardana.entity.bill.enums.BillTypeEnum.PERSONAL_BILL AND b.creatorId = :personalId AND b.number = :number")})
public class Bill implements Serializable {

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
    @Column(name = "name")
    private String name;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private BillTypeEnum type;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "executiontype")
    private BillExecutionTypeEnum executionType;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private BillStatusEnum status;
    @Column(name = "creatorid")
    private Long creatorId;
    @Column(name = "payerid")
    private Long payerId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createddate")
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "executiondate")
    private Date executionDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "duedate")
    private Date dueDate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "latefeetype")
    private BillLateFeeTypeEnum lateFeeType;
    @Column(name = "latefeevalue", precision = 38, scale = 2)
    private BigDecimal lateFeeValue;
    @Column(name = "latefeeday")
    private Integer lateFeeDay;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "latefeelastexecuteddate")
    private Date lateFeeLastExecutedDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payerresponsedate")
    private Date payerResponseDate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "responseby")
    private BillResponseByEnum responseBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "paiddate")
    private Date paidDate;
    @Column(name = "invoiceid")
    private Long invoiceId;
    @Column(name = "transactionid")
    private Long transactionId;
    @Column(name = "billamount")
    private BigDecimal billAmount;
    @Column(name = "totallatefeeamount")
    private BigDecimal totalLateFeeAmount;
    @Column(name = "totalbillamount")
    private BigDecimal totalBillAmount;
    @Lob
    @Column(name = "billdesc")
    private String billDesc;
    @Lob
    @Column(name = "info1")
    private String info1;
    @Lob
    @Column(name = "info2")
    private String info2;
    @Lob
    @Column(name = "info3")
    private String info3;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "payerrejectreason")
    private BillRejectReasonEnum payerRejectReason;
    @Lob
    @Column(name = "payerrejectdesc")
    private String payerRejectDesc;

    public Bill() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BillTypeEnum getType() {
        return type;
    }

    public void setType(BillTypeEnum type) {
        this.type = type;
    }

    public BillExecutionTypeEnum getExecutionType() {
        return executionType;
    }

    public void setExecutionType(BillExecutionTypeEnum executionType) {
        this.executionType = executionType;
    }

    public BillStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BillStatusEnum status) {
        this.status = status;
    }

    public String getCreatorId() {
        return creatorId == null ? null : String.valueOf(creatorId);
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getPayerId() {
        return payerId == null ? null : String.valueOf(payerId);
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getCreatedDate() {
        return createdDate == null ? null : DATE_FORMAT.format(createdDate);
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getExecutionDate() {
        return executionDate == null ? null : DATE_FORMAT.format(executionDate);
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public String getDueDate() {
        return dueDate == null ? null : DATE_FORMAT.format(dueDate);
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BillLateFeeTypeEnum getLateFeeType() {
        return lateFeeType;
    }

    public void setLateFeeType(BillLateFeeTypeEnum lateFeeType) {
        this.lateFeeType = lateFeeType;
    }

    public String getLateFeeValue() {
        return lateFeeValue == null ? null : lateFeeValue.toPlainString();
    }

    public void setLateFeeValue(BigDecimal lateFeeValue) {
        this.lateFeeValue = lateFeeValue;
    }

    public String getLateFeeDay() {
        return lateFeeDay == null ? null : lateFeeDay.toString();
    }

    public void setLateFeeDay(Integer lateFeeDay) {
        this.lateFeeDay = lateFeeDay;
    }

    public String getLateFeeLastExecutedDate() {
        return lateFeeLastExecutedDate == null ? null : DATE_FORMAT.format(lateFeeLastExecutedDate);
    }

    public void setLateFeeLastExecutedDate(Date lateFeeLastExecutedDate) {
        this.lateFeeLastExecutedDate = lateFeeLastExecutedDate;
    }

    public String getPayerResponseDate() {
        return payerResponseDate == null ? null : DATE_FORMAT.format(payerResponseDate);
    }

    public void setPayerResponseDate(Date payerResponseDate) {
        this.payerResponseDate = payerResponseDate;
    }

    public BillResponseByEnum getResponseBy() {
        return responseBy;
    }

    public void setResponseBy(BillResponseByEnum responseBy) {
        this.responseBy = responseBy;
    }

    public String getPaidDate() {
        return paidDate == null ? null : DATE_FORMAT.format(paidDate);
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public String getInvoiceId() {
        return invoiceId == null ? null : String.valueOf(invoiceId);
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getTransactionId() {
        return transactionId == null ? null : String.valueOf(transactionId);
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getBillAmount() {
        return billAmount == null ? null : billAmount.toPlainString();
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    public String getTotalLateFeeAmount() {
        return totalLateFeeAmount == null ? null : totalLateFeeAmount.toPlainString();
    }

    public void setTotalLateFeeAmount(BigDecimal totalLateFeeAmount) {
        this.totalLateFeeAmount = totalLateFeeAmount;
    }

    public String getTotalBillAmount() {
        return totalBillAmount == null ? null : totalBillAmount.toPlainString();
    }

    public void setTotalBillAmount(BigDecimal totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    public String getBillDesc() {
        return billDesc;
    }

    public void setBillDesc(String billDesc) {
        this.billDesc = billDesc;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getInfo3() {
        return info3;
    }

    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    public BillRejectReasonEnum getPayerRejectReason() {
        return payerRejectReason;
    }

    public void setPayerRejectReason(BillRejectReasonEnum payerRejectReason) {
        this.payerRejectReason = payerRejectReason;
    }

    public String getPayerRejectDesc() {
        return payerRejectDesc;
    }

    public void setPayerRejectDesc(String payerRejectDesc) {
        this.payerRejectDesc = payerRejectDesc;
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
        if (!(object instanceof Bill)) {
            return false;
        }
        Bill other = (Bill) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.bill.Bill[ id=" + id + " ]";
    }

}

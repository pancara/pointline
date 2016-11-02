package id.hardana.entity.bni;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by pancara on 1/28/16.
 */

@Entity
@Table(name = "bnipaymentlog")
public class BniPaymentLog implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Lob
    @Basic
    @Column(name = "rawrequest")
    private String rawRequest;

    @Column(name = "transactionid")
    private String transactionId;

    @Column(name = "virtualaccount")
    private String virtualAccount;

    @Column(name = "customername")
    private String customerName;

    @Column(name = "transactionamount")
    private BigDecimal transactionAmount;

    @Column(name = "paymentamount")
    private BigDecimal paymentAmount;

    @Column(name = "cumulativepaymentamount")
    private BigDecimal cumulativePaymentAmount;

    @Column(name = "paymentntb")
    private String paymentNtb;

    @Column(name = "datetimepayment")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimePayment;

    @Column(name = "createddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private BniPaymentStatusEnum status = BniPaymentStatusEnum.NEW;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "response")
    private BniPaymentResponseEnum response;

    public BniPaymentLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public void setRawRequest(String rawRequestPayload) {
        this.rawRequest = rawRequestPayload;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getVirtualAccount() {
        return virtualAccount;
    }

    public void setVirtualAccount(String virtualAccount) {
        this.virtualAccount = virtualAccount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getCumulativePaymentAmount() {
        return cumulativePaymentAmount;
    }

    public void setCumulativePaymentAmount(BigDecimal cumulativePaymentAmount) {
        this.cumulativePaymentAmount = cumulativePaymentAmount;
    }

    public String getPaymentNtb() {
        return paymentNtb;
    }

    public void setPaymentNtb(String paymentNtb) {
        this.paymentNtb = paymentNtb;
    }

    public Date getDatetimePayment() {
        return datetimePayment;
    }

    public void setDatetimePayment(Date datetimePayment) {
        this.datetimePayment = datetimePayment;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public BniPaymentStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BniPaymentStatusEnum status) {
        this.status = status;
    }

    public BniPaymentResponseEnum getResponse() {
        return response;
    }

    public void setResponse(BniPaymentResponseEnum response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BniPaymentLog that = (BniPaymentLog) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BniPayment{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", virtualAccount='" + virtualAccount + '\'' +
                ", customerName='" + customerName + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", paymentAmount=" + paymentAmount +
                ", cumulativePaymentAmount=" + cumulativePaymentAmount +
                ", paymentNtb='" + paymentNtb + '\'' +
                ", datetimePayment=" + datetimePayment +
                ", createdDate=" + createdDate +
                '}';
    }
}

package id.hardana.entity.jswitch;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by pancara on 7/6/16.
 */

@Entity
@Table(name = "jswitch_payment")
public class JSwitchPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Version
    private Integer version;

    @Column(name = "inquiryId", nullable = false)
    private Long inquiryId;

    @Column(name = "transactionId")
    private Long transactionId;

    @Column(name = "denomination")
    private BigDecimal denomination;

    @Column(name = "jswitchTotalAmount")
    private BigDecimal jswitchTotalAmount;

    @Column(name = "clientTotalAmount")
    private BigDecimal clientTotalAmount;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private JSwitchPaymentStatusEnum status;

    @Lob
    @Column(name = "response")
    private String response;

    @Lob
    @Column(name = "chartTable")
    private String chartTable;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdatedAt")
    private Date lastUpdatedAt;

    public JSwitchPayment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId) {
        this.inquiryId = inquiryId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getDenomination() {
        return denomination;
    }

    public void setDenomination(BigDecimal totalAmountPayment) {
        this.denomination = totalAmountPayment;
    }

    public BigDecimal getJswitchTotalAmount() {
        return jswitchTotalAmount;
    }

    public void setJswitchTotalAmount(BigDecimal totalAmount) {
        this.jswitchTotalAmount = totalAmount;
    }

    public BigDecimal getClientTotalAmount() {
        return clientTotalAmount;
    }

    public void setClientTotalAmount(BigDecimal clientTotalAmount) {
        this.clientTotalAmount = clientTotalAmount;
    }

    public JSwitchPaymentStatusEnum getStatus() {
        return status;
    }

    public void setStatus(JSwitchPaymentStatusEnum status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    public String getChartTable() {
        return chartTable;
    }

    public void setChartTable(String chartTable) {
        this.chartTable = chartTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JSwitchPayment that = (JSwitchPayment) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "JSwitchPayment{" +
                "id=" + id +
                ", inquiryId=" + inquiryId +
                ", transactionId=" + transactionId +
                ", denomination=" + denomination +
                ", jswitchTotalAmount=" + jswitchTotalAmount +
                ", clientTotalAmount=" + clientTotalAmount +
                ", status=" + status +
                ", response='" + response + '\'' +
                ", createdAt=" + createdAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}

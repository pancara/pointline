package id.hardana.entity.jswitch;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by pancara on 7/6/16.
 */

@Entity
@Table(name = "jswitch_inquiry")
public class JSwitchInquiry implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Version
    private Integer version;

    @Column(name = "personalId", nullable = false)
    private Long personalId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private JSwitchInquiryStatusEnum status;

    @Column(name = "accountNumber")
    private String accountNumber;

    @Column(name = "itemType")
    private String itemType;

    @Column(name = "mobilePhone")
    private String mobilePhone;

    @Column(name = "sessionId")
    private String sessionId;

    @Column(name ="totalAmount")
    private BigDecimal totalAmount;

    @Lob
    @Column(name = "responseText")
    private String responseText;

    @Lob
    @Column(name = "chartTable")
    private String chartTable;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdatedAt")
    private Date lastUpdatedAt;

    public JSwitchInquiry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(Long personalId) {
        this.personalId = personalId;
    }

    public JSwitchInquiryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(JSwitchInquiryStatusEnum status) {
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getChartTable() {
        return chartTable;
    }

    public void setChartTable(String chartTable) {
        this.chartTable = chartTable;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JSwitchInquiry that = (JSwitchInquiry) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "JSwitchInquiry{" +
                "id=" + id +
                ", personalId=" + personalId +
                ", status=" + status +
                ", accountNumber='" + accountNumber + '\'' +
                ", itemType='" + itemType + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", totalAmount=" + totalAmount +
                ", responseText='" + responseText + '\'' +
                ", createdAt=" + createdAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}

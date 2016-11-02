package id.hardana.entity.jswitch;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pancara on 7/17/16.
 */

@Entity
@Table(name = "jswitch_advice")
public class JSwitchAdvice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Version
    private Integer version;

    @Column(name = "paymentId")
    private Long paymentId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private JSwitchAdviceStatusEnum status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdated")
    private Date lastUpdated;

    @Lob
    @Column(name = "responseText")
    private String responseText;

    public JSwitchAdvice() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public JSwitchAdviceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(JSwitchAdviceStatusEnum status) {
        this.status = status;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JSwitchAdvice that = (JSwitchAdvice) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

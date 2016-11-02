package id.hardana.entity.transaction;

import javax.persistence.*;

import java.io.Serializable;

/**
 * Created by pancara on 11/02/15.
 */
@Entity
@Table(name = "transactionmerchanthistory")
public class TransactionMerchantHistory implements Serializable {
    public static final String TRANSACTION = "T";
    public static final String DISBURSEMENT = "D";
    public static final String IMBURSEMENT = "I";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "merchantid")
    private Long merchantId;

    @Column(name = "refid", nullable = false)
    private Long refId;

    @Column(name = "reftype", nullable = false)
    private String refType;

    public TransactionMerchantHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long referencedId) {
        this.refId = referencedId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String type) {
        this.refType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionMerchantHistory that = (TransactionMerchantHistory) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

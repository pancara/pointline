package id.hardana.entity.document;

import javax.persistence.*;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 *
 */
@Entity
@Table(name = "mposreportdocument")
public class MposReportDocument implements Serializable {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "filename")
    private String filename;
    @Column(name = "merchantid")
    private Long merchantId;
    @Column(name = "outletid")
    private Long outletId;
    @Column(name = "operatorid")
    private Long operatorId;
    @Column(name = "uploaddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    public MposReportDocument() {
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMerchantId() {
        return merchantId == null ? null : String.valueOf(merchantId);
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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

    public String getUploadDate() {
        return uploadDate == null ? null : DATE_FORMAT.format(uploadDate);
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MposReportDocument that = (MposReportDocument) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

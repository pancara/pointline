package id.hardana.entity.document;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pancara on 12/02/15.
 */
@Entity
@Table(name = "carddocument")
public class CardDocument implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "filename")
    private String filename;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "operator")
    private String operator;

    @Column(name = "uploaddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    public CardDocument() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardDocument that = (CardDocument) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

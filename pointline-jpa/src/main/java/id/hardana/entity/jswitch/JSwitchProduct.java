package id.hardana.entity.jswitch;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pancara on 6/29/16.
 */

@Entity
@Table(name = "jswitch_product")
public class JSwitchProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Version
    private Integer version;

    @Column(name ="code", unique = true)
    private String code;

    @Column(name ="name")
    private String name;

    @Column(name ="enabled")
    private Boolean enabled;

    @Column(name = "listed_in_jswitch") // removed by jswitch
    private Boolean listedInJSwitch;

    @Column(name ="createdAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name ="updatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;

    public JSwitchProduct() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getListedInJSwitch() {
        return listedInJSwitch;
    }

    public void setListedInJSwitch(Boolean removed) {
        this.listedInJSwitch = removed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JSwitchProduct that = (JSwitchProduct) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

package id.hardana.entity.jswitch;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by pancara on 7/4/16.
 */
@Entity
@Table(name = "jswitch_login")
public class JSwitchLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Version
    private Integer version;

    @Column(name = "jswitchUid")
    private String jswitchUid;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "salt")
    private String salt;

    @Column(name = "loginAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginAt;

    @Column(name = "active")
    private Boolean active;

    public JSwitchLogin() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJswitchUid() {
        return jswitchUid;
    }

    public void setJswitchUid(String jswitchUid) {
        this.jswitchUid = jswitchUid;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(Date loginAt) {
        this.loginAt = loginAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JSwitchLogin that = (JSwitchLogin) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

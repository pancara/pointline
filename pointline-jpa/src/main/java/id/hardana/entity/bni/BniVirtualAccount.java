package id.hardana.entity.bni;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by pancara on 2/26/16.
 */

@Entity
@Table(name = "bnivirtualaccount")
public class BniVirtualAccount implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "personalvirtualaccountid", unique = true)
    private Long personalVirtualAccountId;

    @Column(name = "virtualaccount", unique = true)
    private String virtualAccount;

    public BniVirtualAccount() {
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

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getPersonalVirtualAccountId() {
        return personalVirtualAccountId;
    }

    public void setPersonalVirtualAccountId(Long personalVirtualAccountId) {
        this.personalVirtualAccountId = personalVirtualAccountId;
    }

    public String getVirtualAccount() {
        return virtualAccount;
    }

    public void setVirtualAccount(String virtualAccount) {
        this.virtualAccount = virtualAccount;
    }

    @Override
    public String toString() {
        return "BniVirtualAccount{" +
                "id=" + id +
                ", version=" + version +
                ", personalVirtualAccountId=" + personalVirtualAccountId +
                ", virtualAccount='" + virtualAccount + '\'' +
                '}';
    }
}

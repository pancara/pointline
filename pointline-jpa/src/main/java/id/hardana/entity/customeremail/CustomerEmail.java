/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.customeremail;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * @author Arya
 */
@Entity
@Table(name = "customeremail")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "CustomerEmail.findAll", query = "SELECT i FROM CustomerEmail i")
})
public class CustomerEmail implements Serializable {
  
    private static final long serialVersionUID = 1L;
    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Size(max = 35)
    @Column(name="datetime")
    private String dateTime;
    @Size(max = 29)
    @Column(name="name")
    private String name;
    @Size(max = 64)

    public CustomerEmail() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}

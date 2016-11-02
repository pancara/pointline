/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.personal;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author hanafi
 */
@Entity
@Table(name = "secretquestion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SecretQuestion.findAll", query = "SELECT s FROM SecretQuestion s"),
    @NamedQuery(name = "SecretQuestion.findById", query = "SELECT s FROM SecretQuestion s WHERE s.id = :id"),
    @NamedQuery(name = "SecretQuestion.findByQuestion", query = "SELECT s FROM SecretQuestion s WHERE s.question = :question")})
public class SecretQuestion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;
    @Lob
    @Column(name = "question")
    private String question;

    public SecretQuestion() {
    }

    public SecretQuestion(Integer id) {
        this.id = id;
    }

    public String getId() {
        return id == null ? null : String.valueOf(id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SecretQuestion)) {
            return false;
        }
        SecretQuestion other = (SecretQuestion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id.hardana.entity.profiles.Secretquestion[ id=" + id + " ]";
    }
    
}

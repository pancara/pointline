/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.registration;

import java.io.Serializable;

/**
 *
 * @author Trisna
 */
public class BNIRegisterObject implements Serializable {

    private Long personalInfoId;

    public Long getPersonalInfoId() {
        return personalInfoId;
    }

    public void setPersonalInfoId(Long personalInfoId) {
        this.personalInfoId = personalInfoId;
    }

}

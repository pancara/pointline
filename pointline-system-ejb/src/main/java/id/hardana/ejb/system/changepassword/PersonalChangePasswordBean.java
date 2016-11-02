/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.changepassword;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class PersonalChangePasswordBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int passwordExpirationDays = 90; // Days

    public String changePassword(String account, String hashOldPassword, String hashNewPassword) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response.toString();
        }

        List<PersonalInfo> personalInfosSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfosSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response.toString();
        }
        PersonalInfo personalInfoSearch = personalInfosSearch.get(0);

        Long personalInfoId = Long.parseLong(personalInfoSearch.getId());

        List<Personal> personalsSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        Personal personalSearch = personalsSearch.get(0);
        em.refresh(personalSearch);
        
        String hashOldPasswordFromDB = personalSearch.getPassword1();
        String hashOldPassword2FromDB = personalSearch.getPassword2();
        String hashOldPassword3FromDB = personalSearch.getPassword3();
        if (!hashOldPassword.equals(hashOldPasswordFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PASSWORD.getResponseStatus());
            return response.toString();
        }
        if (hashNewPassword.equals(hashOldPassword2FromDB)
                || hashNewPassword.equalsIgnoreCase(hashOldPassword3FromDB)
                || hashNewPassword.equals(hashOldPasswordFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.NEW_PASSWORD_EQUALS_WITH_LAST_3_PASSWORD.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(Calendar.DAY_OF_YEAR, passwordExpirationDays);
        Date passwordExpiredDate = c.getTime();
        personalSearch.setPassword1(hashNewPassword);
        personalSearch.setPassword2(hashOldPasswordFromDB);
        personalSearch.setPassword3(hashOldPassword2FromDB);
        personalSearch.setPasswordExpired(passwordExpiredDate);
        personalSearch.setLastupdated(nowDate);
        em.merge(personalSearch);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response.toString();
    }

}

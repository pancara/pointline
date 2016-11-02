/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.changepassword;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.entity.profile.admin.Administrator;
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

public class AdminChangePasswordBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int passwordExpirationDays = 90; // Days

    public String changePassword(String userName, String hashOldPassword, String hashNewPassword) {
        JSONObject response = new JSONObject();

        List<Administrator> adminsSearch = em.createNamedQuery("Personal.findByUserName", Administrator.class)
                .setParameter("userName", userName)
                .getResultList();

        if (adminsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response.toString();
        }
        Administrator adminSearch = adminsSearch.get(0);
        em.refresh(adminSearch);
        String hashOldPasswordFromDB = adminSearch.getPassword1();
        String hashOldPassword2FromDB = adminSearch.getPassword2();
        String hashOldPassword3FromDB = adminSearch.getPassword3();
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
        adminSearch.setPassword1(hashNewPassword);
        adminSearch.setPassword2(hashOldPasswordFromDB);
        adminSearch.setPassword3(hashOldPassword2FromDB);
        adminSearch.setPasswordExpired(passwordExpiredDate);
        em.merge(adminSearch);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response.toString();
    }

}

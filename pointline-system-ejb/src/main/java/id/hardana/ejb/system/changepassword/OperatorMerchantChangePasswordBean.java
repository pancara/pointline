/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.changepassword;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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

public class OperatorMerchantChangePasswordBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public String changePassword(String merchantCode, String userName,
            String hashOldPassword, String hashNewPassword) {
        JSONObject response = new JSONObject();

        List<Merchant> merchantsSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response.toString();
        }
        Merchant merchantSearch = merchantsSearch.get(0);
        Long merchantId = Long.parseLong(merchantSearch.getId());

        List<Operator> operatorsSearch = em.createNamedQuery("Operator.findByUserNameAndMerchantId", Operator.class)
                .setParameter("userName", userName)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (operatorsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response.toString();
        }
        Operator operatorSearch = operatorsSearch.get(0);
        em.refresh(operatorSearch);

        String hashOldPasswordFromDB = operatorSearch.getPassword();
        if (!hashOldPassword.equals(hashOldPasswordFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PASSWORD.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        operatorSearch.setPassword(hashNewPassword);
        operatorSearch.setLastUpdated(nowDate);
        em.merge(operatorSearch);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response.toString();
    }

}

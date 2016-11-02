/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.viewer;

import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.IdentityTypeEnum;
import id.hardana.entity.profile.merchant.LineOfBusiness;
import id.hardana.entity.profile.other.Bank;
import id.hardana.entity.profile.other.City;
import id.hardana.entity.profile.other.Province;
import id.hardana.entity.profile.personal.SecretQuestion;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.ArrayList;
import java.util.EnumSet;
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

public class ViewerBean {

    private final String STATUS_KEY = "status";
    private final String PROVINCE_KEY = "province";
    private final String CITY_KEY = "city";
    private final String SECRET_QUESTION_KEY = "secretQuestion";
    private final String BANK_KEY = "bank";
    private final String ID_TYPE_KEY = "idType";
    private final String LOB_KEY = "lineOfBusiness";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public String viewProvince() {
        JSONObject response = new JSONObject();

        List<Province> provinceSearch = em.createNamedQuery("Province.findAll", Province.class)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PROVINCE_KEY, provinceSearch);
        return response.toString();
    }

    public String viewCity(String provinceId) {
        JSONObject response = new JSONObject();

        Long provinceIdLong;
        try {
            provinceIdLong = Long.parseLong(provinceId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PROVINCE_ID.getResponseStatus());
            return response.toString();
        }

        List<City> citySearch = em.createQuery("SELECT c FROM City c WHERE c.provinceId = :provinceId", City.class)
                .setParameter("provinceId", provinceIdLong)
                .getResultList();

        if (citySearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PROVINCE_ID.getResponseStatus());
            return response.toString();
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(CITY_KEY, citySearch);
        return response.toString();
    }

    public String viewSecretQuestion() {
        JSONObject response = new JSONObject();

        List<SecretQuestion> secretQuestionSearch = em.createNamedQuery("SecretQuestion.findAll", SecretQuestion.class)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(SECRET_QUESTION_KEY, secretQuestionSearch);
        return response.toString();
    }

    public String viewBank() {
        JSONObject response = new JSONObject();

        List<Bank> bankSearch = em.createNamedQuery("Bank.findAll", Bank.class)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(BANK_KEY, bankSearch);
        return response.toString();
    }
    
    public String viewIdentityType() {
        JSONObject response = new JSONObject();

         List<IdentityTypeEnum> listIdentityType = new ArrayList<IdentityTypeEnum>(EnumSet.allOf(IdentityTypeEnum.class));

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(ID_TYPE_KEY, listIdentityType);
        return response.toString();
    }

    public String viewLoB() {
        JSONObject response = new JSONObject();

        List<LineOfBusiness> lobSearch = em.createNamedQuery("LineOfBusiness.findAll", LineOfBusiness.class)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(LOB_KEY, lobSearch);
        return response.toString();
    }

}

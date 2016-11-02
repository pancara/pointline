/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.profile;

import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.JSONTools;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.entity.profile.other.Bank;
import id.hardana.entity.profile.other.City;
import id.hardana.entity.profile.other.Province;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalBank;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.profile.personal.PersonalVirtualAccount;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class ViewProfileBean {

    private final String STATUS_KEY = "status";
    private final String PIN_STATUS_KEY = "pinStatus";
    private final String PROFILE_KEY = "profile";
    private final String BANK_KEY = "bank";
    private final String VA_KEY = "virtualAccount";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject viewProfile(String account) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }
        PersonalInfo personalInfo = personalInfoSearch.get(0);
        em.refresh(personalInfo);
        Long personalInfoId = Long.parseLong(personalInfo.getId());

        String personalCityName = null;
        String provinceIdString = null;
        String personalProvinceName = null;
        try {
            Long cityId = Long.parseLong(personalInfo.getCityId());

            List<City> citySearch = em.createNamedQuery("City.findById", City.class)
                    .setParameter("id", cityId)
                    .getResultList();
            City personalCity = citySearch.get(0);
            personalCityName = personalCity.getName();
            provinceIdString = personalCity.getProvinceId();

            List<Province> provinceSearch = em.createNamedQuery("Province.findById", Province.class)
                    .setParameter("id", Long.parseLong(provinceIdString))
                    .getResultList();
            Province personalProvince = provinceSearch.get(0);
            personalProvinceName = personalProvince.getName();
        } catch (Exception e) {
        }

        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();

        Personal personal = personalSearch.get(0);
        em.refresh(personal);
        boolean pinAvailable = false;
        if (personal.getPin() != null) {
            pinAvailable = true;
        }

        List<PersonalBank> personalBankData = em.createQuery("SELECT p FROM PersonalBank p WHERE p.personalInfoId = :personalInfoId", PersonalBank.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();

        List<PersonalVirtualAccount> pvaSearch = em.createQuery("SELECT p FROM PersonalVirtualAccount p WHERE p.personalInfoId = :personalInfoId", PersonalVirtualAccount.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        String vaNumber = null;
        if (!pvaSearch.isEmpty()) {
            PersonalVirtualAccount pva = pvaSearch.get(0);
            vaNumber = pva.getAccountNumber();
        }

        List<PersonalInfo> personalInfoData = new ArrayList<>();
        List<Personal> personalData = new ArrayList<>();
        personalInfoData.add(personalInfo);
        personalData.add(personal);
        JSONObject temporaryJSON = new JSONObject();

        temporaryJSON.put("data1", personalInfoData);
        temporaryJSON.put("data2", personalData);
        if (!personalBankData.isEmpty()) {
            temporaryJSON.put("data3", personalBankData);
        }

        JSONArray arrayPersonalInfoData = temporaryJSON.getJSONArray("data1");
        JSONObject personalInfoObject = arrayPersonalInfoData.getJSONObject(0);
        personalInfoObject.remove("id");
        personalInfoObject.remove("identityPicture");
        personalInfoObject.remove("lastUpdated");
        personalInfoObject.remove("profilePic");
        personalInfoObject.remove("signature");
        personalInfoObject.put("city", personalCityName);
        personalInfoObject.put("provinceId", provinceIdString);
        personalInfoObject.put("province", personalProvinceName);

        JSONArray arrayPersonalData = temporaryJSON.getJSONArray("data2");
        JSONObject personalObject = arrayPersonalData.getJSONObject(0);
        personalObject.remove("id");
        personalObject.remove("activationCode");
        personalObject.remove("isDeleted");
        personalObject.remove("lastupdated");
        personalObject.remove("password1");
        personalObject.remove("password2");
        personalObject.remove("password3");
        personalObject.remove("passwordExpired");
        personalObject.remove("personalInfoId");
        personalObject.remove("pin");

        JSONObject mergePersonalData = JSONTools.mergeJSONObject(personalInfoObject, personalObject);

        if (!personalBankData.isEmpty()) {
            JSONArray arrayPersonalBankData = temporaryJSON.getJSONArray("data3");
            for (int i = 0; i < arrayPersonalBankData.length(); i++) {
                JSONObject personalBankObject = arrayPersonalBankData.getJSONObject(i);

                List<Bank> bankData = em.createNamedQuery("Bank.findById", Bank.class)
                        .setParameter("id", Long.parseLong(personalBankObject.getString("bankId")))
                        .getResultList();
                Bank bank = bankData.get(0);
                String bankName = bank.getBankName();

                personalBankObject.remove("id");
                personalBankObject.remove("personalInfoId");
                personalBankObject.remove("isDeleted");
                personalBankObject.put("bank", bankName);
            }
            mergePersonalData.put(BANK_KEY, arrayPersonalBankData);
        }
        
        mergePersonalData.put(VA_KEY, vaNumber);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PIN_STATUS_KEY, pinAvailable);
        response.put(PROFILE_KEY, mergePersonalData);
        return response;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.profile;

import id.hardana.ejb.system.tools.EmailFormatValidator;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.entity.profile.enums.IdentityTypeEnum;
import id.hardana.entity.profile.other.Bank;
import id.hardana.entity.profile.other.City;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalBank;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class EditProfileBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    @Resource
    private EJBContext context;

    public String edit(String account, String firstName, String lastName, String email,
            String gender, String address, String zipCode, String cityId,
            String placeOfBirth, String dateOfBirth, String identityType, String identityNumber,
            String npwp, String bankId, String bankBranch, String bankAccHolderName, String bankAccNumber) {
        JSONObject response = new JSONObject();

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response.toString();
        }

        if (placeOfBirth.length() > 50
                || firstName.length() > 30 || lastName.length() > 30
                || zipCode.length() > 5 || npwp.length() > 20) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response.toString();
        }

        boolean isValidEmail = EmailFormatValidator.validate(email);
        if (!isValidEmail) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_EMAIL_ADDRESS.getResponseStatus());
            return response.toString();
        }

        if (!gender.equals("M") && !gender.equals("F")) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_GENDER_FORMAT.getResponseStatus());
            return response.toString();
        }
        Character genderChar = gender.charAt(0);

        Long cityIdLong;
        try {
            cityIdLong = Long.parseLong(cityId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_CITY_ID.getResponseStatus());
            return response.toString();
        }
        List<City> citySearch = em.createNamedQuery("City.findById", City.class)
                .setParameter("id", cityIdLong)
                .getResultList();
        if (citySearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_CITY_ID.getResponseStatus());
            return response.toString();
        }

        Date dateOfBirthDt;
        try {
            dateOfBirthDt = DATE_FORMAT.parse(dateOfBirth);
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
            return response.toString();
        }

        int idTypeInt;
        IdentityTypeEnum identityTypeEnum;
        try {
            idTypeInt = Integer.parseInt(identityType);
            identityTypeEnum = IdentityTypeEnum.getIdentityType(idTypeInt);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_ID_TYPE.getResponseStatus());
            return response.toString();
        }

        Long bankIdLong;
        try {
            bankIdLong = Long.parseLong(bankId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_BANK_ID.getResponseStatus());
            return response.toString();
        }
        List<Bank> bankSearch = em.createNamedQuery("Bank.findById", Bank.class)
                .setParameter("id", bankIdLong)
                .getResultList();
        if (bankSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_BANK_ID.getResponseStatus());
            return response.toString();
        }

        List< PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response.toString();
        }

        Date nowDate = new Date();
        PersonalInfo personalInfo = personalInfoSearch.get(0);

        try {
            personalInfo.setFirstName(firstName);
            personalInfo.setLastName(lastName);
            personalInfo.setEmail(email);
            personalInfo.setLastUpdated(nowDate);
            personalInfo.setGender(genderChar);
            personalInfo.setAddress(address);
            personalInfo.setZipCode(zipCode);
            personalInfo.setCityId(cityIdLong);
            personalInfo.setPlaceOfBirth(placeOfBirth);
            personalInfo.setDateOfBirth(dateOfBirthDt);
            personalInfo.setIdentityType(identityTypeEnum);
            personalInfo.setIdNumber(identityNumber);
            personalInfo.setNpwp(npwp);
            em.merge(personalInfo);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response.toString();
        }

        Long personalInfoId = Long.parseLong(personalInfo.getId());
        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();

        Personal personal = personalSearch.get(0);

        try {
            personal.setLastupdated(nowDate);
            em.merge(personal);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response.toString();
        }

        List<PersonalBank> personalBankSearch = em.createQuery("SELECT p FROM PersonalBank p WHERE p.personalInfoId = :personalInfoId", PersonalBank.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        PersonalBank personalBank;
        if (personalBankSearch.isEmpty()) {
            personalBank = new PersonalBank();
            personalBank.setPersonalInfoId(personalInfoId);
            personalBank.setBankId(bankIdLong);
            personalBank.setAccountHolderName(bankAccHolderName);
            personalBank.setAccountNumber(bankAccNumber);
            personalBank.setBankBranch(bankBranch);
            personalBank.setIsDeleted(Boolean.FALSE);
            em.persist(personalBank);
        } else {
            personalBank = personalBankSearch.get(0);
            personalBank.setBankId(bankIdLong);
            personalBank.setAccountHolderName(bankAccHolderName);
            personalBank.setAccountNumber(bankAccNumber);
            personalBank.setBankBranch(bankBranch);
            personalBank.setIsDeleted(Boolean.FALSE);
            em.merge(personalBank);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response.toString();
    }

}

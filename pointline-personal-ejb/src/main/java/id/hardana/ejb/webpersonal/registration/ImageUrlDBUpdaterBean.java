/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.registration;

import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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

public class ImageUrlDBUpdaterBean {

    private final String STATUS_KEY = "status";
    private final int TYPE_ID_CARD = 0;
    private final int TYPE_PROFILE_PICT = 1;
    private final int TYPE_SIGNATURE = 2;
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject idCardImageUpdate(String account, String url) {
        return imageUpdate(account, url, TYPE_ID_CARD);
    }

    public JSONObject profilePictImageUpdate(String account, String url) {
        return imageUpdate(account, url, TYPE_PROFILE_PICT);
    }

    public JSONObject signatureImageUpdate(String account, String url) {
        return imageUpdate(account, url, TYPE_SIGNATURE);
    }

    private JSONObject imageUpdate(String account, String url, int type) {
        JSONObject response = new JSONObject();

        List< PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", account)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }

        try {

            Date nowDate = new Date();
            PersonalInfo personalInfo = personalInfoSearch.get(0);
            em.refresh(personalInfo);
            personalInfo.setLastUpdated(nowDate);
            if (type == TYPE_ID_CARD) {
                personalInfo.setIdentityPicture(url);

                Long personalInfoId = Long.parseLong(personalInfo.getId());
                List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                        .setParameter("personalInfoId", personalInfoId)
                        .getResultList();

                Personal personal = personalSearch.get(0);
                em.refresh(personal);
                personal.setStatusId(PersonalStatusEnum.PENDINGUPGRADE);
                personal.setLastupdated(nowDate);
                em.merge(personal);
            } else if (type == TYPE_PROFILE_PICT) {
                personalInfo.setProfilePic(url);
            } else if (type == TYPE_SIGNATURE) {
                personalInfo.setSignature(url);
            }
            em.merge(personalInfo);

        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

}

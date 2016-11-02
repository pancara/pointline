/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.registration;

import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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

public class ImageUrlDBViewerBean {

    private final String STATUS_KEY = "status";
    private final String URL_KEY = "url";
    private final int TYPE_ID_CARD = 0;
    private final int TYPE_PROFILE_PICT = 1;
    private final int TYPE_SIGNATURE = 2;
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject idCardImageView(String account) {
        return imageView(account, TYPE_ID_CARD);
    }

    public JSONObject profilePictImageView(String account) {
        return imageView(account, TYPE_PROFILE_PICT);
    }

    public JSONObject signatureImageView(String account) {
        return imageView(account, TYPE_SIGNATURE);
    }

    private JSONObject imageView(String account, int type) {
        JSONObject response = new JSONObject();

        List< PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", account)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }

        PersonalInfo personalInfo = personalInfoSearch.get(0);
        em.refresh(personalInfo);
        String urlImage = null;
        if (type == TYPE_ID_CARD) {
            urlImage = personalInfo.getIdentityPicture();
        } else if (type == TYPE_PROFILE_PICT) {
            urlImage = personalInfo.getProfilePic();
        } else if (type == TYPE_SIGNATURE) {
            urlImage = personalInfo.getSignature();
        }

        if (urlImage == null || urlImage.equals("")) {
            response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_URL_IMAGE.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(URL_KEY, urlImage);
        return response;
    }

}

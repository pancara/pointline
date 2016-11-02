/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.ejb.system.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.APIVersion;
import id.hardana.ejb.system.tools.SHA;
import id.hardana.entity.sys.channel.ChannelAPI;
import id.hardana.entity.sys.enums.ApplicationTypeEnum;
import id.hardana.entity.sys.enums.ChannelStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.security.NoSuchAlgorithmException;
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

public class ChannelAPIValidationBean {

    private final String STATUS_KEY = "status";
    private final String CHANNELID_KEY = "channelId";
    private final String API_VERSION_KEY = "apiVersion";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject validateChannel(String applicationKey, String hashApplicationSecret,
            EnumSet<ApplicationTypeEnum> applicationTypes, boolean mustLocalChannel,
            String dateFromClient, String apiVersion) {
        JSONObject response = new JSONObject();

//        Search applicationKey in DB 
        List<ChannelAPI> channelAPIs = em.createNamedQuery("ChannelAPI.findByApplicationKey", ChannelAPI.class)
                .setParameter("applicationKey", applicationKey)
                .getResultList();

        if (channelAPIs.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.UNREGISTERED_APPLICATION.getResponseStatus());
            return response;
        }
        ChannelAPI channelAPI = channelAPIs.get(0);
        em.refresh(channelAPI);

//        Validate applicationSecretHash
        String applicationSecretFromDB = channelAPI.getApplicationSecret();
        String hashSecretFromDB;
        try {
            hashSecretFromDB = SHA.SHA256Hash(SHA.SHA256Hash(applicationKey + SHA.SHA256Hash(applicationSecretFromDB)) + dateFromClient);
        } catch (NoSuchAlgorithmException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response;
        }
        if (!hashApplicationSecret.equalsIgnoreCase(hashSecretFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_APPLICATION_SECRET.getResponseStatus());
            return response;
        }

//        Validate Status Channel
        ChannelStatusEnum status = channelAPI.getStatus();

        if (status.equals(ChannelStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INACTIVE_APPLICATION.getResponseStatus());
            return response;
        }   

//        Validate Application Types
       // channelAPI.setApplicationType(ApplicationTypeEnum.WEBMERCHANT);   //temp for testing
        //channelAPI.setApplicationType(ApplicationTypeEnum.GROUP_MERCHANT);
        
        ApplicationTypeEnum channelApplicationType = channelAPI.getApplicationType();
        Boolean isValidApplicationChannel = false;
        for (ApplicationTypeEnum applicationType : applicationTypes) {
            if (channelApplicationType.equals(applicationType)) {
                isValidApplicationChannel = true;
            }
        }

        if (!isValidApplicationChannel) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_APPLICATION_TYPE.getResponseStatus());
            return response;
        }

//        Validate Local Channel
        Boolean isLocalChannel = Boolean.valueOf(channelAPI.getIsLocalChannel());
        if (isLocalChannel) {
            JSONObject responseValidateApiVersion = validateApiVersion(apiVersion, channelApplicationType);
            String statusResponseValidateApiVersion = responseValidateApiVersion.getString(STATUS_KEY);
            Integer apiVersionInt = responseValidateApiVersion.getInt(API_VERSION_KEY);
            if (!statusResponseValidateApiVersion.equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
                return responseValidateApiVersion;
            }
            response.put(STATUS_KEY, ResponseStatusEnum.VALID_APPLICATION.getResponseStatus());
            response.put(CHANNELID_KEY, channelAPI.getId());
            response.put(API_VERSION_KEY, apiVersionInt);
            return response;
        } else {
            if (mustLocalChannel) {
                response.put(STATUS_KEY, ResponseStatusEnum.OPEN_API_NOT_ALLOWED.getResponseStatus());
                return response;
            } else {
                //        Slot for OPEN API, Outside channel
                response.put(STATUS_KEY, ResponseStatusEnum.OPEN_API_NOT_READY.getResponseStatus());
                return response;
            }
        }

    }
    
    public JSONObject validateChannelMpos(String applicationKey, String hashApplicationSecret,
            EnumSet<ApplicationTypeEnum> applicationTypes, boolean mustLocalChannel,
            String dateFromClient, String apiVersion) {
        JSONObject response = new JSONObject();

//        Search applicationKey in DB 
        List<ChannelAPI> channelAPIs = em.createNamedQuery("ChannelAPI.findByApplicationKey", ChannelAPI.class)
                .setParameter("applicationKey", applicationKey)
                .getResultList();

        if (channelAPIs.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.UNREGISTERED_APPLICATION.getResponseStatus());
            return response;
        }
        ChannelAPI channelAPI = channelAPIs.get(0);
        em.refresh(channelAPI);

//        Validate applicationSecretHash
        String applicationSecretFromDB = channelAPI.getApplicationSecret();
        String hashSecretFromDB;
        try {
            hashSecretFromDB = SHA.SHA256Hash(SHA.SHA256Hash(applicationKey + SHA.SHA256Hash(applicationSecretFromDB)) + dateFromClient);
        } catch (NoSuchAlgorithmException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response;
        }
        if (!hashApplicationSecret.equalsIgnoreCase(hashSecretFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_APPLICATION_SECRET.getResponseStatus());
            return response;
        }

//        Validate Status Channel
        ChannelStatusEnum status = channelAPI.getStatus();

        if (status.equals(ChannelStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INACTIVE_APPLICATION.getResponseStatus());
            return response;
        }

//        Validate Application Types
        channelAPI.setApplicationType(ApplicationTypeEnum.MPOS);   //temp for testing
        ApplicationTypeEnum channelApplicationType = channelAPI.getApplicationType();
        Boolean isValidApplicationChannel = false;
        for (ApplicationTypeEnum applicationType : applicationTypes) {
            if (channelApplicationType.equals(applicationType)) {
                isValidApplicationChannel = true;
            }
        }

        if (!isValidApplicationChannel) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_APPLICATION_TYPE.getResponseStatus());
            return response;
        }

//        Validate Local Channel
        Boolean isLocalChannel = Boolean.valueOf(channelAPI.getIsLocalChannel());
        if (isLocalChannel) {
            JSONObject responseValidateApiVersion = validateApiVersion(apiVersion, channelApplicationType);
            String statusResponseValidateApiVersion = responseValidateApiVersion.getString(STATUS_KEY);
            Integer apiVersionInt = responseValidateApiVersion.getInt(API_VERSION_KEY);
            if (!statusResponseValidateApiVersion.equals(ResponseStatusEnum.VALID_APPLICATION.getResponseStatus())) {
                return responseValidateApiVersion;
            }
            response.put(STATUS_KEY, ResponseStatusEnum.VALID_APPLICATION.getResponseStatus());
            response.put(CHANNELID_KEY, channelAPI.getId());
            response.put(API_VERSION_KEY, apiVersionInt);
            return response;
        } else {
            if (mustLocalChannel) {
                response.put(STATUS_KEY, ResponseStatusEnum.OPEN_API_NOT_ALLOWED.getResponseStatus());
                return response;
            } else {
                //        Slot for OPEN API, Outside channel
                response.put(STATUS_KEY, ResponseStatusEnum.OPEN_API_NOT_READY.getResponseStatus());
                return response;
            }
        }

    }

    private JSONObject validateApiVersion(String apiVersion, ApplicationTypeEnum applicationType) {
        JSONObject response = new JSONObject();
        Integer apiVersionInt = 0;
        Integer minimumApiVersion = 0;
        try {
            apiVersionInt = Integer.parseInt(apiVersion);
            if (apiVersionInt < 106) {
                apiVersionInt = 0;
            }
        } catch (Exception e) {
            apiVersionInt = 0;
        }

        if (applicationType.equals(ApplicationTypeEnum.WEBMERCHANT)) {
            minimumApiVersion = APIVersion.getMinimumApiVersionMerchantWeb();
        } else if (applicationType.equals(ApplicationTypeEnum.MPOS)) {
            minimumApiVersion = APIVersion.getMinimumApiVersionMPOS();
        } else if (applicationType.equals(ApplicationTypeEnum.PERSONAL)) {
            minimumApiVersion = APIVersion.getMinimumApiVersionPersonal();
        } else if (applicationType.equals(ApplicationTypeEnum.WEBPERSONAL)) {
            minimumApiVersion = APIVersion.getMinimumApiVersionPersonalWeb();
        } else if (applicationType.equals(ApplicationTypeEnum.WEBADMIN)) {
            minimumApiVersion = APIVersion.getMinimumApiVersionAdmin();
        }

        if (apiVersionInt < minimumApiVersion) {
            response.put(STATUS_KEY, ResponseStatusEnum.OPEN_API_NOT_ALLOWED.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.VALID_APPLICATION.getResponseStatus());
        response.put(API_VERSION_KEY, apiVersionInt);
        return response;
    }

}

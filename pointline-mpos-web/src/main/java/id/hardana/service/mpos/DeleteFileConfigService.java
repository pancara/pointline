/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos;

import id.hardana.ejb.mpos.reportdoc.DeleteFileConfigBean;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.service.mpos.interceptor.NullCheckInterceptor;
import id.hardana.service.mpos.log.LoggingInterceptor;
import java.io.File;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Arya
 */
@Stateless
@Path("deletefileconfig")
@Interceptors({LoggingInterceptor.class, NullCheckInterceptor.class})

public class DeleteFileConfigService {

    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private DeleteFileConfigBean deleteFileConfigBean;
    private final String STATUS_KEY = "status";
    private final String dirConfig = "mposconfig";
    
    public DeleteFileConfigService() {
    }

    @POST
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String deleteFileConfig(@FormParam("applicationKey") String applicationKey,
            @FormParam("merchantCode") String merchantCode,
            @FormParam("userName") String userName,
            @FormParam("sessionId") String sessionId,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId,
            @FormParam("fileName") String fileName
            )
    {
        
        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);
        
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            
            String pathOfConfigFile;
            File f = new File(System.getProperty("com.sun.aas.instanceRoot"));
            String rootdir = f.getAbsolutePath() + File.separator + "docroot" + File.separator;
            pathOfConfigFile = rootdir + dirConfig + File.separator + merchantCode + File.separator;

            return deleteFileConfigBean.deleteFileConfigData(pathOfConfigFile, fileName).toString();    
        }
        return responseValidate.toString();
    }

}

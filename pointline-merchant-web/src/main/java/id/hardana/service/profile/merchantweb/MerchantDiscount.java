package id.hardana.service.profile.merchantweb;

import id.hardana.ejb.merchantweb.MerchantDiscountBean;
import id.hardana.ejb.merchantweb.interceptor.InterceptNull;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.json.JSONObject;

/**
 *
 * @author pancara
 */

@Stateless
@Path("merchantdiscount")
@Interceptors(LoggingInterceptor.class)
public class MerchantDiscount {
    
    @EJB
    private OperatorMerchantValidationBean omvb;
    
    @EJB
    private MerchantDiscountBean merchantDiscountBean;
    
    @POST
    @Path("/add")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String addDiscount(@FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             
                             @FormParam("merchantCode") String merchantCode,
                             @FormParam("discountName") String discountName,
                             @FormParam("discountDesc") String discountDesc,
                             @FormParam("discountValue") String discountValue, // value as rupiah or percentage
                             @FormParam("discountValueType") String discountValueType, // int
                             @FormParam("discountCalculationType") String discountCalculationType, // int
                             @FormParam("discountApplyType") String discountApplyType, // int
                             @FormParam("discountColor") String discountColor,
                             @FormParam("discountEnabled") String discountEnabled,
                             @FormParam("discountAuto") String discountAuto) {
        // local validation
        
        // block invalid values here (pricing type must be integer, etc)
        
        // sys validation
        JSONObject validation = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(validation.getString("status")))){
          return validation.toString();
        }

        // call ejb
        JSONObject json = merchantDiscountBean.addDiscount(merchantCode, discountName, discountDesc, discountColor, 
                discountValue, discountValueType, discountCalculationType, discountApplyType,  discountEnabled, discountAuto);
        
        return json.toString();
    }
    
    @POST
    @Path("/edit")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String editDiscount(@FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             
                             @FormParam("discountId") String discountId,
                             @FormParam("merchantCode") String merchantCode,
                             @FormParam("discountName") String discountName,
                             @FormParam("discountDesc") String discountDesc,
                             @FormParam("discountValue") String discountValue, // value as rupiah or percentage
                             @FormParam("discountValueType") String discountValueType, // int
                             @FormParam("discountCalculationType") String discountCalculationType, // int
                             @FormParam("discountApplyType") String discountApplyType, // int
                             @FormParam("discountColor") String discountColor,
                             @FormParam("discountEnabled") String discountEnabled,
                             @FormParam("discountAuto") String discountAuto) {
        // local validation
        
        // block invalid values here (pricing type must be integer, etc)
        
        // sys validation
        JSONObject validation = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(validation.getString("status")))){
          return validation.toString();
        }

        // call ejb
        JSONObject json = merchantDiscountBean.editDiscount(merchantCode, discountId, discountName, discountDesc, discountColor,
                discountValue, discountValueType, discountCalculationType, discountApplyType, discountEnabled, discountAuto);
        
        return json.toString();
    }
    
    @POST
    @Path("/delete")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String deleteDiscount(@FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             
                             @FormParam("discountId") String discountId,
                             @FormParam("merchantCode") String merchantCode) {
        JSONObject validation = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(validation.getString("status")))){
          return validation.toString();
        }

        // call ejb
        JSONObject json = merchantDiscountBean.deleteDiscount(discountId, merchantCode);        
        return json.toString();
    }
    
    @POST
    @Path("/get")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String getDiscounts(@FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             @FormParam("merchantCode") String merchantCode,
                             
                             @FormParam("keyword") String keyword,
                             @FormParam("page") String page,
                             @FormParam("count") String count) {
        
        JSONObject validation = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        
        if(!("VALID".equals(validation.getString("status")))){
          return validation.toString();
        }

        
        Integer nPage = 1;
        if (page != null && !page.isEmpty()) {
            nPage = Integer.valueOf(page);
        }
        
        Integer nCount = 10;
        if (count != null && !count.isEmpty()) {
            nCount = Integer.valueOf(count);
        }
        JSONObject json = merchantDiscountBean.getDiscounts(merchantCode, keyword, nPage, nCount);        
        return json.toString();
    }
    
}

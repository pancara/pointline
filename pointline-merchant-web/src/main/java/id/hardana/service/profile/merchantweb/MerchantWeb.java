 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.profile.merchantweb;

import id.hardana.ejb.merchantweb.MerchantFacade;
import id.hardana.ejb.merchantweb.MerchantGroupBean;
import id.hardana.ejb.merchantweb.MerchantProductFacade;
import id.hardana.ejb.merchantweb.MerchantWebEJB;
import id.hardana.ejb.merchantweb.interceptor.InterceptNull;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.profile.enums.GroupMerchantToMerchantStatusEnum;
import id.hardana.service.profile.log.LoggingInterceptor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import org.json.JSONObject;

/**
 * BREAST Web Service
 *
 * @author hanafi
 */
@Stateless
@Path("/merchantweb")
@Interceptors(LoggingInterceptor.class)
public class MerchantWeb {

    @Context
    private UriInfo context;
    
    private String STATUSSUCCESS = "SUCCESS";
    
    /**
     * Creates a new instance of MerchantWeb
     */
    public MerchantWeb() {
    }

    @EJB
    MerchantWebEJB mwEJB;
    
    @EJB
    MerchantFacade merchantFacade;
    
    @EJB
    MerchantProductFacade merchantProductFacade;
    
    @EJB
    OperatorMerchantValidationBean omvb;
    
    @EJB
    MerchantGroupBean merchantGroupBean;
    //@EJB
    //MerchantOwnerValidationBean movb;
    
    @POST
    @Path("/registerMerchant")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String registerMerchant(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token,
                                    //@FormParam("brandName") String brandName,
                                    @FormParam("brandDescription") String brandDesc,
                                    @FormParam("lineOfBusiness") String lob, // integer
                                    @FormParam("majorProduct") String majorProd,
                                    @FormParam("ownerIdNumber") String ownerIdNumber,
                                    @FormParam("ownerIdType") String ownerIdType, // integer
                                    @FormParam("ownerName") String ownerName,
                                    @FormParam("ownerPhone") String ownerPhone,
                                    @FormParam("ownerEmail") String ownerEmail,
                                    // reps
                                    @FormParam("contactIdNumber") String contactIdNumber,
                                    @FormParam("contactIdType") String contactIdType, // integer
                                    @FormParam("contactName") String contactName,
                                    @FormParam("contactPhone") String contactPhone
                                    //@FormParam("contactEmail") String contactEmail
                                    ){
        
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
//        merchantFacade.registerMerchant(merchantCode, brandName, brandDesc, lob, majorProd, ownerIdNumber, ownerIdType, ownerName, ownerPhone, ownerEmail,
//                                        repIdNumber, repIdType, repName, repPhone, repEmail);
        merchantFacade.registerMerchant(merchantCode, brandDesc, lob, majorProd, ownerIdNumber, ownerIdType, ownerName, ownerPhone, ownerEmail,
                                        contactIdNumber, contactIdType, contactName, contactPhone);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", STATUSSUCCESS);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/getProfile")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getMerchant(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
    
      // intercept null

      // sys validation
      JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
      
      if(!("VALID".equals(status.getString("status")))){
          return status.toString();
      }
      
      HashMap<String,Object> retVal;
      
      retVal = merchantFacade.getMerchant(merchantCode);
      
      return new JSONObject(retVal).toString();

      // for basic testing only
      //// create a new config : id, config key , config value , lastomodified
      //mwEJB.sillyMethod("configkeytext", "configvaluetext", new Date());
      //  
      //JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
      //
      //System.out.println(status);
      //
      //return mwEJB.testMethod("test OK");
    
    }
    
    @POST
    @Path("/editProfile")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String editMerchant(@FormParam("merchantCode") String merchantCode,
                              @FormParam("applicationKey") String applicationKey,
                              @FormParam("userName") String userName,
                              @FormParam("session") String session,
                              @FormParam("token") String token,
                              @FormParam("brandName") String brandName,
                              @FormParam("brandDescription") String brandDesc,
                              @FormParam("lineOfBusiness") String lob, // integer
                              @FormParam("majorProduct") String majorProd,
                              @FormParam("ownerIdNumber") String ownerIdNumber,
                              @FormParam("ownerIdType") String ownerIdType, // integer
                              @FormParam("ownerName") String ownerName,
                              @FormParam("ownerPhone") String ownerPhone,
                              @FormParam("ownerEmail") String ownerEmail,
                              // reps
                              @FormParam("contactIdNumber") String contactIdNumber,
                              @FormParam("contactIdType") String contactIdType, // integer
                              @FormParam("contactName") String contactName,
                              @FormParam("contactPhone") String contactPhone,
                              @FormParam("contactEmail") String contactEmail  ){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        merchantFacade.editMerchant(merchantCode, brandName, brandDesc, lob, majorProd, ownerIdNumber, ownerIdType, ownerName,
                                    ownerPhone, ownerEmail, contactIdNumber, contactIdType, contactName, contactPhone, contactEmail);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", STATUSSUCCESS);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/registerBank")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String registerBank(@FormParam("merchantCode") String merchantCode,
                               @FormParam("applicationKey") String applicationKey,
                               @FormParam("userName") String userName,
                               @FormParam("session") String session,
                               @FormParam("token") String token,
                                @FormParam("bankAccountNumber") String bankAccNum,
                                @FormParam("bankAccountName") String bankAccName,
                                @FormParam("bankId") String bankId,  // integer
                                @FormParam("bankBranch") String bankBranch){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        merchantFacade.registerBank(merchantCode, bankAccNum, bankAccName, bankId, bankBranch);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", STATUSSUCCESS);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/registerOutlet")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String registerOutlet(@FormParam("merchantCode") String merchantCode,
                                 @FormParam("applicationKey") String applicationKey,
                                 @FormParam("userName") String userName,
                                 @FormParam("session") String session,
                                 @FormParam("token") String token,
                                  @FormParam("outletName") String outletName,
                                  @FormParam("outletAddress") String outletAddress,
                                  @FormParam("outletCity") String outletCity, // integer
                                  @FormParam("outletLongitude") String outletLongitude,
                                  @FormParam("outletLatitude") String outletLatitude,
                                  @FormParam("outletStatus") String outletStatus){ // integer
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        merchantFacade.registerOutlet(merchantCode, outletName, outletAddress, outletCity, outletLongitude, outletLatitude, outletStatus);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", STATUSSUCCESS);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/getOutlet")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getOutlet(@FormParam("merchantCode") String merchantCode,
                            @FormParam("applicationKey") String applicationKey,
                            @FormParam("userName") String userName,
                            @FormParam("session") String session,
                            @FormParam("token") String token){

        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,Object> retVal = merchantFacade.getOutlet(merchantCode);
        
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/editOutlet")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String editOutlet(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             @FormParam("outletId") String outletId,
                             @FormParam("outletName") String outletName,
                             @FormParam("outletAddress") String outletAddress,
                             @FormParam("outletCity") String outletCity, // integer
                             @FormParam("outletLongitude") String outletLongitude,
                             @FormParam("outletLatitude") String outletLatitude,
                             @FormParam("outletStatus") String outletStatus){ // integer
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        
        
        HashMap<String,String> retVal = merchantFacade.editOutlet(outletId, outletName, outletAddress, outletCity, outletLongitude, outletLatitude, outletStatus);
        
        return new JSONObject(retVal).toString();
        
    }
    
    
    @POST
    @Path("/registerOperatorLevel")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String registerOperatorGroup(@FormParam("merchantCode") String merchantCode,
                                        @FormParam("applicationKey") String applicationKey,
                                        @FormParam("userName") String userName,
                                        @FormParam("session") String session,
                                        @FormParam("token") String token,
                                        @FormParam("operatorLevelName") String opLevelName,
                                        @FormParam("operatorLevelDescription") String opLevelDesc){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        //JSONObject statusOwner = movb.validateMerchantWebOwner(userName);
        // call ejb
        merchantFacade.registerOperatorLevel(merchantCode, opLevelName, opLevelDesc);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", STATUSSUCCESS);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/getOperatorLevel")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getOperatorLevel(@FormParam("merchantCode") String merchantCode,
                                   @FormParam("applicationKey") String applicationKey,
                                   @FormParam("userName") String userName,
                                   @FormParam("session") String session,
                                   @FormParam("token") String token){
        
        
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,Object> retVal = merchantFacade.getOperatorLevel(merchantCode);
        
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/editOperatorLevel")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String editOperatorLevel(@FormParam("merchantCode") String merchantCode,
                                    @FormParam("applicationKey") String applicationKey,
                                    @FormParam("userName") String userName,
                                    @FormParam("session") String session,
                                    @FormParam("token") String token,
                                    @FormParam("operatorLevelId") String opLevelId,
                                    @FormParam("operatorLevelName") String opLevelName,
                                    @FormParam("operatorLevelDescription") String opLevelDesc){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        HashMap<String,String> retVal = merchantFacade.editOperatorLevel(opLevelId, opLevelName, opLevelDesc);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/registerOperator")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String registerOperator(@FormParam("merchantCode") String merchantCode,
                                   @FormParam("applicationKey") String applicationKey,
                                   @FormParam("userName") String userName,
                                   @FormParam("session") String session,
                                   @FormParam("token") String token,
                                    @FormParam("userNameReg") String userNameReg,
                                    @FormParam("operatorPassword") String opPassword,
                                    @FormParam("operatorFullName") String opFullName,
                                    @FormParam("employeeNumber") String employeeNum,
                                    @FormParam("operatorLevel") String opLevel, // integer
                                    @FormParam("operatorPic") String opPic,
                                    @FormParam("operatorStatus") String opStatus){ // integer

        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        String retStat = merchantFacade.registerOperator(merchantCode, userNameReg, opPassword, opFullName, employeeNum, opLevel, opPic, opStatus);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", retStat);
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/getOperator")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getOperator(@FormParam("merchantCode") String merchantCode,
                              @FormParam("applicationKey") String applicationKey,
                              @FormParam("userName") String userName,
                              @FormParam("session") String session,
                              @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        
        HashMap<String,Object> retVal = merchantFacade.getOperator(merchantCode);
        
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/editOperator")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String editOperator(@FormParam("merchantCode") String merchantCode,
                               @FormParam("applicationKey") String applicationKey,
                               @FormParam("userName") String userName,
                               @FormParam("session") String session,
                               @FormParam("token") String token,
                               @FormParam("operatorId") String opId,
                               @FormParam("userName") String userNameReg,
                               @FormParam("operatorPassword") String opPassword,
                               @FormParam("operatorRealName") String opRealName,
                               @FormParam("employeeNumber") String employeeNum,
                               @FormParam("operatorLevel") String opLevel, // integer
                               @FormParam("operatorPic") String opPic,
                               @FormParam("operatorStatus") String opStatus){ // integer
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,String> retVal = merchantFacade.editOperator(opId, userNameReg, opPassword, opRealName, employeeNum, opLevel, opPic, opStatus);
        
        return new JSONObject(retVal).toString();
        
    }
    
    
    @POST
    @Path("/addProductCategory")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String addProductCategory(@FormParam("merchantCode") String merchantCode,
                                     @FormParam("applicationKey") String applicationKey,
                                     @FormParam("userName") String userName,
                                     @FormParam("session") String session,
                                     @FormParam("token") String token,
                                     @FormParam("categoryName") String categoryName,
                                     @FormParam("categoryDescription") String categoryDesc,
                                     @FormParam("colorCode") String colorCode){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        String retStat = merchantProductFacade.addProductCategory(merchantCode, categoryName, categoryDesc, colorCode);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", retStat);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/getProductCategories")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getProductCategories(@FormParam("merchantCode") String merchantCode,
                                     @FormParam("applicationKey") String applicationKey,
                                     @FormParam("userName") String userName,
                                     @FormParam("session") String session,
                                     @FormParam("token") String token){
        
        
        // don't forget to return catId
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,Object> retVal = merchantProductFacade.getProductCategories(merchantCode);
        
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/editProductCategory")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String editProductCategory(@FormParam("merchantCode") String merchantCode,
                                      @FormParam("applicationKey") String applicationKey,
                                      @FormParam("userName") String userName,
                                      @FormParam("session") String session,
                                      @FormParam("token") String token,
                                      @FormParam("categoryId") String categoryId, // integer
                                      @FormParam("categoryName") String categoryName,
                                      @FormParam("categoryDescription") String categoryDesc,
                                      @FormParam("colorCode") String colorCode,
                                      @FormParam("categoryStatus") String categoryStatus){
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,String> retVal = merchantProductFacade.editProductCategory(categoryId, categoryName, categoryDesc, colorCode, categoryStatus);
        
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/deleteProductCategory")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String deleteProductCategory(@FormParam("merchantCode") String merchantCode,
                                      @FormParam("applicationKey") String applicationKey,
                                      @FormParam("userName") String userName,
                                      @FormParam("session") String session,
                                      @FormParam("token") String token,
                                      @FormParam("categoryId") String categoryId){
    
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,String> retVal = merchantProductFacade.deleteProductCategory(merchantCode, categoryId);
                
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/addProductItem")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String addProductItem(@FormParam("merchantCode") String merchantCode,
                                 @FormParam("applicationKey") String applicationKey,
                                 @FormParam("userName") String userName,
                                 @FormParam("session") String session,
                                 @FormParam("token") String token,
                                 @FormParam("categoryId") String categoryId, // integer
                                 @FormParam("itemName") String itemName,
                                 @FormParam("itemCode") String itemCode,
                                 @FormParam("itemSupplyPrice") String itemSupplyPrice,
                                 @FormParam("itemMarkUpPrice") String itemMarkUpPrice,
                                 @FormParam("itemSalesPrice") String itemSalesPrice,
                                 @FormParam("itemPicture") String itemPic,
                                 @FormParam("itemStatus") String itemStatus, // integer
                                 @FormParam("itemDescription") String itemDesc){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        // signature too long to insert status directly
        String retStat = merchantProductFacade.addProductItem(merchantCode, categoryId, itemName, itemCode, itemSupplyPrice, itemMarkUpPrice,
                                                itemSalesPrice, itemPic, itemStatus, itemDesc);
        
//        HashMap<String,String> retVal = new HashMap<>();
//        
//        retVal.put("status", retStat);
        
        return retStat;
        
    }
    
    @POST
    @Path("/getProductItems")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getProductItems(@FormParam("merchantCode") String merchantCode,
                                 @FormParam("applicationKey") String applicationKey,
                                 @FormParam("userName") String userName,
                                 @FormParam("session") String session,
                                 @FormParam("token") String token,
                                 @FormParam("categoryId") String categoryId){
        // don't forget to return pricingId
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,Object> retVal = merchantProductFacade.getItems(merchantCode, categoryId);
        
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/editProductItem")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String editProductItem(@FormParam("merchantCode") String merchantCode,
                                  @FormParam("applicationKey") String applicationKey,
                                  @FormParam("userName") String userName,
                                  @FormParam("session") String session,
                                  @FormParam("token") String token,
                                  @FormParam("categoryId") String categoryId, // integer
                                  @FormParam("itemId") String itemId, // integer
                                  @FormParam("itemName") String itemName,
                                  @FormParam("itemCode") String itemCode,
                                  @FormParam("itemSupplyPrice") String itemSupplyPrice,
                                  @FormParam("itemMarkUpPrice") String itemMarkUpPrice,
                                  @FormParam("itemSalesPrice") String itemSalesPrice,
                                  @FormParam("itemPicture") String itemPic,
                                  @FormParam("itemStatus") String itemStatus, // integer
                                  @FormParam("itemDescription") String itemDesc){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,String> retVal = merchantProductFacade.editProductItem(merchantCode, categoryId, itemId, itemName, itemCode, itemSupplyPrice, itemMarkUpPrice, 
                                                itemSalesPrice, itemPic, itemStatus, itemDesc);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/deleteProductItem")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String deleteProductItem(@FormParam("merchantCode") String merchantCode,
                                      @FormParam("applicationKey") String applicationKey,
                                      @FormParam("userName") String userName,
                                      @FormParam("session") String session,
                                      @FormParam("token") String token,
                                      @FormParam("itemId") String itemId){
    
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,String> retVal = merchantProductFacade.deleteProductItem(merchantCode, itemId);
                
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/addPricing")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String addPricing(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             @FormParam("pricingName") String pricingName,
                             @FormParam("pricingType") String pricingType, // integer
                             @FormParam("pricingValue") String pricingValue,
                             @FormParam("pricingLevel") String pricingLevel, // integer
                             @FormParam("pricingStatus") String pricingStatus // integer
    ){ 
        // local validation
        
        // block invalid values here (pricing type must be integer, etc)
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        String retStat = merchantProductFacade.addPricing(merchantCode, pricingName, pricingType, pricingValue, pricingStatus, pricingLevel);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", retStat);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/getPricings")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getPricings(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        // don't forget to return pricingId
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,Object> retVal = merchantProductFacade.getPricings(merchantCode);
        
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/editPricing")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String editPricing(@FormParam("merchantCode") String merchantCode,
                              @FormParam("applicationKey") String applicationKey,
                              @FormParam("userName") String userName,
                              @FormParam("session") String session,
                              @FormParam("token") String token,
                              @FormParam("pricingId") String pricingId, // integer
                              @FormParam("pricingName") String pricingName,
                              @FormParam("pricingType") String pricingType, // integer
                              @FormParam("pricingValue") String pricingValue,
                              @FormParam("pricingLevel") String pricingLevel,
                              @FormParam("pricingStatus") String pricingStatus){ // integer
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,String> retVal = merchantProductFacade.editPricing(merchantCode, pricingId, pricingName, pricingType, pricingValue, pricingStatus, pricingLevel);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/deletePricing")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String deletePricing(@FormParam("merchantCode") String merchantCode,
                                      @FormParam("applicationKey") String applicationKey,
                                      @FormParam("userName") String userName,
                                      @FormParam("session") String session,
                                      @FormParam("token") String token,
                                      @FormParam("pricingId") String pricingId){
    
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        HashMap<String,String> retVal = merchantProductFacade.deletePricing(merchantCode, pricingId);
                
        return new JSONObject(retVal).toString();
    }
    
    //uploadOperatorPic
    //uploadProductItemPic
    
    @POST
    @Path("/getAllPrivileges")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getAllPrivileges(@FormParam("merchantCode") String merchantCode,
                              @FormParam("applicationKey") String applicationKey,
                              @FormParam("userName") String userName,
                              @FormParam("session") String session,
                              @FormParam("token") String token){
        
        // local validation
        
        // sys validation
        //JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        //if(!("VALID".equals(status.getString("status")))){
        //  return status.toString();
        //}

        // call ejb
        return merchantFacade.getAllPrivileges(merchantCode);
        
    }
    
    @POST
    @Path("/registerCompany")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String registerCompany(@FormParam("merchantCode") String merchantCode,
                                  @FormParam("applicationKey") String applicationKey,
                                  @FormParam("userName") String userName,
                                  @FormParam("session") String session,
                                  @FormParam("token") String token,
                                  @FormParam("companyName") String companyName,
                                  @FormParam("companyAddress") String companyAddress,
                                  @FormParam("companySIUP") String companySIUP,
                                  @FormParam("companyAkta") String companyAkta,
                                  @FormParam("companyNPWP") String companyNPWP,
                                  @FormParam("companyCityId") String companyCityId, // integer
                                  @FormParam("companyZipCode") String companyZipCode,
                                  @FormParam("companyPhone") String companyPhone,
                                  @FormParam("companyEmail") String companyEmail,
                                  @FormParam("companyOwner") String companyOwner,
                                  @FormParam("password") String password){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }

        // call ejb
        merchantFacade.registerCompany(companyName, companyAddress, companySIUP, companyAkta, companyNPWP, 
                                        companyCityId, companyZipCode, companyPhone, companyEmail, companyOwner, password);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", STATUSSUCCESS);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/addCompany")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String addCompany(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             @FormParam("companyCode") String companyCode,
                             @FormParam("companyPassword") String companyPassword){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        merchantFacade.addCompany(merchantCode, companyCode, companyPassword);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", STATUSSUCCESS);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/editCompany")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String editCompany(@FormParam("merchantCode") String merchantCode,
                              @FormParam("applicationKey") String applicationKey,
                              @FormParam("userName") String userName,
                              @FormParam("session") String session,
                              @FormParam("token") String token,
                              @FormParam("companyCode") String companyCode,
                              @FormParam("companyPassword") String companyPassword,
                              @FormParam("companyName") String companyName,
                              @FormParam("companyAddress") String companyAddress,
                              @FormParam("companySIUP") String companySIUP,
                              @FormParam("companyAkta") String companyAkta,
                              @FormParam("companyNPWP") String companyNPWP,
                              @FormParam("companyCityId") String companyCityId, // integer
                              @FormParam("companyZipCode") String companyZipCode,
                              @FormParam("companyPhone") String companyPhone,
                              @FormParam("companyEmail") String companyEmail,
                              @FormParam("companyOwner") String companyOwner){
        
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        merchantFacade.editCompany(companyCode, companyPassword, companyName, companyAddress, companySIUP, companyAkta, companyNPWP, 
                                    companyCityId, companyZipCode, companyPhone, companyEmail, companyOwner);
        
        HashMap<String,String> retVal = new HashMap<>();
        
        retVal.put("status", STATUSSUCCESS);
        
        return new JSONObject(retVal).toString();
        
    }
    
    @POST
    @Path("/getCompany")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getCompany(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token,
                             @FormParam("companyPassword") String companyPassword){
        return null;
    }
    
    
    @POST
    @Path("/getOrderProduct")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getOrderProduct(@FormParam("merchantCode") String merchantCode,
                                  @FormParam("applicationKey") String applicationKey,
                                  @FormParam("userName") String userName,
                                  @FormParam("session") String session,
                                  @FormParam("token") String token,
                                  @FormParam("operatorName") String operatorName,
                                  @FormParam("lastUpdated") String lastUpdated ){
    
        // local validation
        
        // sys validation
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);

        //if something or other
        if(!("VALID".equals(status.getString("status")))){
          return status.toString();
        }
        
        // call ejb
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        HashMap<String,String> retVal = new HashMap<>();
        
        Date lastUpdateDate;
        try {
            lastUpdateDate = sdf.parse(lastUpdated);
        } catch (ParseException ex) {
            Logger.getLogger(MerchantWeb.class.getName()).log(Level.SEVERE, null, ex);
            retVal.put("status", "WRONG DATE SENT");
            return new JSONObject(retVal).toString();
        }
        
        return merchantProductFacade.getOrderProduct(merchantCode, lastUpdateDate);
        
        //return null;
    }
    
    @POST
    @Path("/getPendingGroupList")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getPendingGroupList(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if(!("VALID".equals(status.getString("status")))){
            return status.toString();
        }
       
        HashMap<String,Object> retVal =  merchantGroupBean.getGroups(merchantCode, GroupMerchantToMerchantStatusEnum.PENDING);
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/getGroupList")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String getGroupList(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("token") String token){
        
        JSONObject status = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if(!("VALID".equals(status.getString("status")))){
            return status.toString();
        }

        HashMap<String,Object> retVal =  merchantGroupBean.getGroups(merchantCode, GroupMerchantToMerchantStatusEnum.ACCEPTED);
        return new JSONObject(retVal).toString();
    }
    
    @POST
    @Path("/updateStatusMerchant")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    @Interceptors(InterceptNull.class)
    public String updateStatusFromMerchant(@FormParam("merchantCode") String merchantCode,
                             @FormParam("applicationKey") String applicationKey,
                             @FormParam("userName") String userName,
                             @FormParam("session") String session,
                             @FormParam("groupId") String groupId, 
                             @FormParam("status") String status,
                             @FormParam("token") String token){
        
        JSONObject statusValidator = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if(!("VALID".equals(statusValidator.getString("status")))){
          return statusValidator.toString();
        }
        
        String retUpdate = merchantGroupBean.updateStatusFromMerchant(groupId, merchantCode, Integer.parseInt(status), new Date());
        HashMap<String,String> retVal = new HashMap<>();
        if (retUpdate.equalsIgnoreCase(STATUSSUCCESS)) {
            retVal.put("status", STATUSSUCCESS);
        } else {
            retVal.put("status", "UPDATE_FROM_MERCHANT_FAILED");
        } 
        return new JSONObject(retVal).toString();
    }
    
    /**
     * Retrieves representation of an instance of id.hardana.service.profile.merchantweb.MerchantWeb
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of MerchantWeb
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}

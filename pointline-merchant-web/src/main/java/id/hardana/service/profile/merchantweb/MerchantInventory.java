/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.profile.merchantweb;

import id.hardana.ejb.merchantweb.inventory.InventoryItemBean;
import id.hardana.ejb.merchantweb.inventory.InventoryLogBean;
import id.hardana.ejb.merchantweb.inventory.InventorySettingBean;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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
 * @author Trisna
 */
@Stateless
@Path("inventory")
@Interceptors(LoggingInterceptor.class)

public class MerchantInventory {

    @EJB
    OperatorMerchantValidationBean omvb;
    @EJB
    private InventorySettingBean inventorySettingBean;
    @EJB
    private InventoryItemBean inventoryItemBean;
    @EJB
    private InventoryLogBean inventoryLogBean;

    private final String STATUS_KEY = "status";

    public MerchantInventory() {
    }

    @POST
    @Path("viewmerchantsetting")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewMerchantSetting(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventorySettingBean.viewMerchantSetting(merchantCode, userName).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("editmerchantsetting")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String editMerchantSetting(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("isActive") String isActive) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventorySettingBean.editMerchantSetting(merchantCode, userName, isActive).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("viewalloutletsetting")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewAllOutletSetting(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventorySettingBean.viewAllOutletSetting(merchantCode, userName).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("editoutletsetting")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String editOutletSetting(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("outletSettings") String outletSettings) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventorySettingBean.editOutletSetting(merchantCode, userName, outletSettings).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("viewitemsbyoutlet")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewItemsByOutlet(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("outletId") String outletId) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventoryItemBean.viewItemsByOutlet(merchantCode, userName, outletId).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("viewallitems")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String viewAllItems(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventoryItemBean.viewAllItems(merchantCode, userName).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("restockitem")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String restockItem(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("items") String items) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventoryItemBean.restockItem(merchantCode, userName, items).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("editminimumstockitem")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String editMinimumStockItem(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("items") String items) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventoryItemBean.editMinimumStockItem(merchantCode, userName, items).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("edititemsetting")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String editItemSetting(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("items") String items) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventoryItemBean.editItemSetting(merchantCode, userName, items).toString();
        }
        return responseValidate.toString();
    }

    @POST
    @Path("inventorylog")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces("application/json")
    public String inventoryLog(@FormParam("merchantCode") String merchantCode,
            @FormParam("applicationKey") String applicationKey,
            @FormParam("userName") String userName,
            @FormParam("session") String session,
            @FormParam("token") String token,
            @FormParam("limit") String limit,
            @FormParam("page") String page,
            @FormParam("startDate") String startDateString,
            @FormParam("endDate") String endDateString) {

        JSONObject responseValidate = omvb.validateOperatorMerchantWeb(applicationKey, merchantCode, userName, session, token);
        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            return inventoryLogBean.inventoryLog(merchantCode, limit, page, startDateString, endDateString).toString();
        }
        return responseValidate.toString();
    }

}

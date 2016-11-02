/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.hardana.ejb.merchantweb.inventory.extension.MerchantInventorySettingHolder;
import id.hardana.ejb.merchantweb.inventory.extension.OutletInventorySettingHolder;
import id.hardana.ejb.merchantweb.inventory.extension.OutletSettingRequestHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.inventory.InventoryLog;
import id.hardana.entity.inventory.InventoryMerchant;
import id.hardana.entity.inventory.InventoryOutlet;
import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

public class InventorySettingBean {

    private final String STATUS_KEY = "status";
    private final String MERCHANT_KEY = "merchant";
    private final String OPERATOR_ID_KEY = "operatorId";
    private final String DATA_KEY = "data";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject viewMerchantSetting(String merchantCode, String userName) {
        JSONObject response = new JSONObject();

        Date nowDate = new Date();

        JSONObject validateResponse = validateMerchantAndOperator(merchantCode, userName);
        if (!validateResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateResponse;
        }

        Merchant merchant = (Merchant) validateResponse.get(MERCHANT_KEY);
        Long merchantId = Long.parseLong(merchant.getId());
        Long operatorId = validateResponse.getLong(OPERATOR_ID_KEY);

        List<MerchantInventorySettingHolder> merchantSettingSearch = em.createQuery("SELECT NEW "
                + "id.hardana.ejb.merchantweb.inventory.extension.MerchantInventorySettingHolder"
                + "(m.code, m.brandName, o.userName, i.lastUpdated, i.isActive) "
                + "FROM InventoryMerchant i JOIN Merchant m ON m.id = i.merchantId "
                + "JOIN Operator o ON o.id = i.updatedBy WHERE i.merchantId = :merchantId",
                MerchantInventorySettingHolder.class)
                .setParameter("merchantId", merchantId)
                .getResultList();
        MerchantInventorySettingHolder merchantSetting;
        if (merchantSettingSearch.isEmpty()) {
            InventoryMerchant inventoryMerchant = new InventoryMerchant();
            inventoryMerchant.setMerchantId(merchantId);
            inventoryMerchant.setUpdatedBy(operatorId);
            inventoryMerchant.setLastUpdated(nowDate);
            inventoryMerchant.setIsActive(Boolean.FALSE);
            em.persist(inventoryMerchant);
            merchantSetting = new MerchantInventorySettingHolder(merchantCode, merchant.getBrandName(),
                    userName, nowDate, Boolean.FALSE);
        } else {
            merchantSetting = merchantSettingSearch.get(0);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, new JSONObject(new Gson().toJson(merchantSetting)));
        return response;

    }

    public JSONObject editMerchantSetting(String merchantCode, String userName, String isActive) {
        JSONObject response = new JSONObject();

        Boolean isActiveBoolean;
        try {
            isActiveBoolean = Boolean.valueOf(isActive);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();

        JSONObject validateResponse = validateMerchantAndOperator(merchantCode, userName);
        if (!validateResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateResponse;
        }

        Merchant merchant = (Merchant) validateResponse.get(MERCHANT_KEY);
        Long merchantId = Long.parseLong(merchant.getId());
        Long operatorId = validateResponse.getLong(OPERATOR_ID_KEY);

        List<InventoryMerchant> merchantSettingSearch = em.createQuery("SELECT i FROM InventoryMerchant i "
                + "WHERE i.merchantId = :merchantId", InventoryMerchant.class)
                .setParameter("merchantId", merchantId)
                .getResultList();
        InventoryMerchant merchantSetting;
        if (merchantSettingSearch.isEmpty()) {
            merchantSetting = new InventoryMerchant();
            merchantSetting.setMerchantId(merchantId);
            merchantSetting.setUpdatedBy(operatorId);
            merchantSetting.setLastUpdated(nowDate);
            merchantSetting.setIsActive(isActiveBoolean);
            em.persist(merchantSetting);
        } else {
            merchantSetting = merchantSettingSearch.get(0);
            merchantSetting.setIsActive(isActiveBoolean);
            em.merge(merchantSetting);
        }

        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setClientDateTime(nowDate);
        inventoryLog.setDateTime(nowDate);
        inventoryLog.setDescription(isActive);
        inventoryLog.setMerchantId(merchantId);
        inventoryLog.setOperatorId(operatorId);
        inventoryLog.setType(InventoryLogTypeEnum.MERCHANT_SETTING_CHANGE);
        em.persist(inventoryLog);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;

    }

    public JSONObject viewAllOutletSetting(String merchantCode, String userName) {
        JSONObject response = new JSONObject();

        JSONObject validateResponse = validateMerchantAndOperator(merchantCode, userName);
        if (!validateResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateResponse;
        }

        Merchant merchant = (Merchant) validateResponse.get(MERCHANT_KEY);
        Long merchantId = Long.parseLong(merchant.getId());

        List<OutletInventorySettingHolder> outletSettingSearch = em.createQuery("SELECT NEW "
                + "id.hardana.ejb.merchantweb.inventory.extension.OutletInventorySettingHolder"
                + "(ot.id, ot.name, o.userName, i.lastUpdated, i.isActive) "
                + "FROM InventoryOutlet i JOIN Outlet ot ON ot.id = i.outletId "
                + "JOIN Operator o ON o.id = i.updatedBy WHERE i.merchantId = :merchantId",
                OutletInventorySettingHolder.class)
                .setParameter("merchantId", merchantId)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, outletSettingSearch);
        return response;

    }

    public JSONObject editOutletSetting(String merchantCode, String userName, String outletSettings) {
        JSONObject response = new JSONObject();

        Date nowDate = new Date();
        List<OutletSettingRequestHolder> listOutletSetting;

        try {
            JSONArray outletSettingArray = new JSONArray(outletSettings);
            Type listTypeOutletSettings = new TypeToken<List<OutletSettingRequestHolder>>() {
            }.getType();
            listOutletSetting = new Gson().fromJson(outletSettingArray.toString(),
                    listTypeOutletSettings);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        JSONObject validateResponse = validateMerchantAndOperator(merchantCode, userName);
        if (!validateResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateResponse;
        }

        Merchant merchant = (Merchant) validateResponse.get(MERCHANT_KEY);
        Long merchantId = Long.parseLong(merchant.getId());
        Long operatorId = validateResponse.getLong(OPERATOR_ID_KEY);

        Set<Long> setOutletId = new HashSet<>();
        List<Long> listOutletId = new ArrayList<>();
        for (OutletSettingRequestHolder outletSetting : listOutletSetting) {
            setOutletId.add(outletSetting.getOutletId());
        }
        listOutletId.addAll(setOutletId);
        
        List<Outlet> outletSearch = em.createQuery("SELECT o FROM Outlet o WHERE "
                + "o.merchantId = :merchantId AND o.id IN :ids", Outlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("ids", listOutletId)
                .getResultList();
        if (outletSearch.size() < listOutletId.size()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_OUTLET_ID.getResponseStatus());
            return response;
        }

        for (OutletSettingRequestHolder outletSetting : listOutletSetting) {
            InventoryOutlet invetoryOutlet;
            List<InventoryOutlet> invetoryOutletSearch = em.createQuery("SELECT i FROM InventoryOutlet i "
                    + "WHERE i.outletId = :outletId", InventoryOutlet.class)
                    .setParameter("outletId", outletSetting.getOutletId())
                    .getResultList();
            if (invetoryOutletSearch.isEmpty()) {
                invetoryOutlet = new InventoryOutlet();
                invetoryOutlet.setIsActive(outletSetting.getIsActive());
                invetoryOutlet.setLastUpdated(nowDate);
                invetoryOutlet.setMerchantId(merchantId);
                invetoryOutlet.setUpdatedBy(operatorId);
                invetoryOutlet.setOutletId(outletSetting.getOutletId());
                em.persist(invetoryOutlet);
            } else {
                invetoryOutlet = invetoryOutletSearch.get(0);
                invetoryOutlet.setIsActive(outletSetting.getIsActive());
                invetoryOutlet.setLastUpdated(nowDate);
                invetoryOutlet.setUpdatedBy(operatorId);
                em.merge(invetoryOutlet);
            }
            
            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setClientDateTime(nowDate);
            inventoryLog.setDateTime(nowDate);
            inventoryLog.setDescription(String.valueOf(outletSetting.getIsActive()));
            inventoryLog.setMerchantId(merchantId);
            inventoryLog.setOperatorId(operatorId);
            inventoryLog.setOutletId(outletSetting.getOutletId());
            inventoryLog.setType(InventoryLogTypeEnum.OUTLET_SETTING_CHANGE);
            em.persist(inventoryLog);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;

    }

    private JSONObject validateMerchantAndOperator(String merchantCode, String userName) {
        JSONObject response = new JSONObject();

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        List<Operator> operatorsSearch = em.createNamedQuery("Operator.findByUserNameAndMerchantId", Operator.class)
                .setParameter("userName", userName)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (operatorsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response;
        }
        Operator operatorSearch = operatorsSearch.get(0);
        Long operatorId = Long.parseLong(operatorSearch.getId());

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(MERCHANT_KEY, merchant);
        response.put(OPERATOR_ID_KEY, operatorId);
        return response;
    }

}

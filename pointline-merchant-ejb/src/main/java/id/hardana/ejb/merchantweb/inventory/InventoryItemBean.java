/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.hardana.ejb.merchantweb.inventory.extension.InventoryItemHolder;
import id.hardana.ejb.merchantweb.inventory.extension.ItemRequestHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.inventory.Inventory;
import id.hardana.entity.inventory.InventoryLog;
import id.hardana.entity.inventory.InventoryLogDetail;
import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import id.hardana.entity.profile.merchant.Items;
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

public class InventoryItemBean {

    private final String STATUS_KEY = "status";
    private final String MERCHANT_ID_KEY = "merchantId";
    private final String OPERATOR_ID_KEY = "operatorId";
    private final String LIST_ITEM_REQUEST_KEY = "listItemRequest";
    private final String DATA_KEY = "data";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject viewItemsByOutlet(String merchantCode, String userName, String outletId) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        try {
            outletIdLong = Long.parseLong(outletId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        JSONObject validateMerchantAndOperatorResponse = validateMerchantAndOperator(merchantCode, userName);
        if (!validateMerchantAndOperatorResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateMerchantAndOperatorResponse;
        }

        Long merchantId = validateMerchantAndOperatorResponse.getLong(MERCHANT_ID_KEY);

        List<Outlet> outletSearch = em.createQuery("SELECT o FROM Outlet o WHERE "
                + "o.merchantId = :merchantId AND o.id = :id", Outlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("id", outletIdLong)
                .getResultList();
        if (outletSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_OUTLET_ID.getResponseStatus());
            return response;
        }

        List<InventoryItemHolder> itemInventorySearch = em.createQuery("SELECT NEW "
                + "id.hardana.ejb.merchantweb.inventory.extension.InventoryItemHolder"
                + "(ot.id, ot.name, it.id, it.name, i.stock, i.minimumStock, o.userName, "
                + "i.lastUpdated, i.isActive) FROM Inventory i JOIN Outlet ot ON ot.id = i.outletId "
                + "JOIN Operator o ON o.id = i.updatedBy JOIN Items it ON i.itemId = it.id "
                + "WHERE i.merchantId = :merchantId AND i.outletId = :outletId",
                InventoryItemHolder.class)
                .setParameter("merchantId", merchantId)
                .setParameter("outletId", outletIdLong)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, itemInventorySearch);
        return response;

    }

    public JSONObject viewAllItems(String merchantCode, String userName) {
        JSONObject response = new JSONObject();

        JSONObject validateMerchantAndOperatorResponse = validateMerchantAndOperator(merchantCode, userName);
        if (!validateMerchantAndOperatorResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateMerchantAndOperatorResponse;
        }

        Long merchantId = validateMerchantAndOperatorResponse.getLong(MERCHANT_ID_KEY);

        List<InventoryItemHolder> itemInventorySearch = em.createQuery("SELECT NEW "
                + "id.hardana.ejb.merchantweb.inventory.extension.InventoryItemHolder"
                + "(ot.id, ot.name, it.id, it.name, i.stock, i.minimumStock, o.userName, "
                + "i.lastUpdated, i.isActive) FROM Inventory i JOIN Outlet ot ON ot.id = i.outletId "
                + "JOIN Operator o ON o.id = i.updatedBy JOIN Items it ON i.itemId = it.id "
                + "WHERE i.merchantId = :merchantId ORDER BY i.outletId",
                InventoryItemHolder.class)
                .setParameter("merchantId", merchantId)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, itemInventorySearch);
        return response;

    }

    public JSONObject restockItem(String merchantCode, String userName, String items) {
        JSONObject response = new JSONObject();

        Date nowDate = new Date();

        JSONObject validateAllResponse = validateAll(merchantCode, userName, items);
        if (!validateAllResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateAllResponse;
        }

        List<ItemRequestHolder> listItemRequest = parseItemsRequest(validateAllResponse.getJSONArray(LIST_ITEM_REQUEST_KEY).toString());
        Long merchantId = validateAllResponse.getLong(MERCHANT_ID_KEY);
        Long operatorId = validateAllResponse.getLong(OPERATOR_ID_KEY);

        for (ItemRequestHolder itemStock : listItemRequest) {
            List<Inventory> invetorySearch = em.createQuery("SELECT i FROM Inventory i "
                    + "WHERE i.merchantId = :merchantId AND i.outletId = :outletId AND i.itemId = :itemId",
                    Inventory.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", itemStock.getOutletId())
                    .setParameter("itemId", itemStock.getItemId())
                    .getResultList();
            Inventory inventory;
            if (!invetorySearch.isEmpty()) {
                inventory = invetorySearch.get(0);
                inventory.setStock(Long.parseLong(inventory.getStock()) + itemStock.getStock());
                inventory.setLastUpdated(nowDate);
                inventory.setUpdatedBy(operatorId);
                em.merge(inventory);
            }
        }

        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setClientDateTime(nowDate);
        inventoryLog.setDateTime(nowDate);
        inventoryLog.setDescription(" ");
        inventoryLog.setMerchantId(merchantId);
        inventoryLog.setOperatorId(operatorId);
        inventoryLog.setOutletId(null);
        inventoryLog.setType(InventoryLogTypeEnum.RESTOCK);
        em.persist(inventoryLog);
        em.flush();

        for (ItemRequestHolder itemStock : listItemRequest) {
            InventoryLogDetail inventoryLogDetail = new InventoryLogDetail();
            inventoryLogDetail.setInventoryLogId(Long.parseLong(inventoryLog.getId()));
            inventoryLogDetail.setDescription(" ");
            inventoryLogDetail.setItemId(itemStock.getItemId());
            inventoryLogDetail.setItemName(itemStock.getItemName());
            inventoryLogDetail.setItemQuantity(itemStock.getStock());
            inventoryLogDetail.setOutletId(itemStock.getOutletId());
            em.persist(inventoryLogDetail);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;

    }

    public JSONObject editMinimumStockItem(String merchantCode, String userName, String items) {
        JSONObject response = new JSONObject();

        Date nowDate = new Date();

        JSONObject validateAllResponse = validateAll(merchantCode, userName, items);
        if (!validateAllResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateAllResponse;
        }

        List<ItemRequestHolder> listItemRequest = parseItemsRequest(validateAllResponse.getJSONArray(LIST_ITEM_REQUEST_KEY).toString());
        Long merchantId = validateAllResponse.getLong(MERCHANT_ID_KEY);
        Long operatorId = validateAllResponse.getLong(OPERATOR_ID_KEY);

        for (ItemRequestHolder itemMinimumStock : listItemRequest) {
            List<Inventory> invetorySearch = em.createQuery("SELECT i FROM Inventory i "
                    + "WHERE i.merchantId = :merchantId AND i.outletId = :outletId AND i.itemId = :itemId",
                    Inventory.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", itemMinimumStock.getOutletId())
                    .setParameter("itemId", itemMinimumStock.getItemId())
                    .getResultList();
            Inventory inventory;
            if (!invetorySearch.isEmpty()) {
                inventory = invetorySearch.get(0);
                inventory.setMinimumStock(itemMinimumStock.getStock());
                inventory.setLastUpdated(nowDate);
                inventory.setUpdatedBy(operatorId);
                em.merge(inventory);
            }
        }

        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setClientDateTime(nowDate);
        inventoryLog.setDateTime(nowDate);
        inventoryLog.setDescription(" ");
        inventoryLog.setMerchantId(merchantId);
        inventoryLog.setOperatorId(operatorId);
        inventoryLog.setOutletId(null);
        inventoryLog.setType(InventoryLogTypeEnum.CHANGE_MINIMUM_STOCK);
        em.persist(inventoryLog);
        em.flush();

        for (ItemRequestHolder itemMinimumStock : listItemRequest) {
            InventoryLogDetail inventoryLogDetail = new InventoryLogDetail();
            inventoryLogDetail.setInventoryLogId(Long.parseLong(inventoryLog.getId()));
            inventoryLogDetail.setDescription(" ");
            inventoryLogDetail.setItemId(itemMinimumStock.getItemId());
            inventoryLogDetail.setItemName(itemMinimumStock.getItemName());
            inventoryLogDetail.setItemQuantity(itemMinimumStock.getStock());
            inventoryLogDetail.setOutletId(itemMinimumStock.getOutletId());
            em.persist(inventoryLogDetail);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;

    }

    public JSONObject editItemSetting(String merchantCode, String userName, String items) {
        JSONObject response = new JSONObject();

        Date nowDate = new Date();

        JSONObject validateAllResponse = validateAll(merchantCode, userName, items);
        if (!validateAllResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateAllResponse;
        }

        List<ItemRequestHolder> listItemRequest = parseItemsRequest(validateAllResponse.getJSONArray(LIST_ITEM_REQUEST_KEY).toString());
        Long merchantId = validateAllResponse.getLong(MERCHANT_ID_KEY);
        Long operatorId = validateAllResponse.getLong(OPERATOR_ID_KEY);

        for (ItemRequestHolder itemRequest : listItemRequest) {
            List<Inventory> invetorySearch = em.createQuery("SELECT i FROM Inventory i "
                    + "WHERE i.merchantId = :merchantId AND i.outletId = :outletId AND i.itemId = :itemId",
                    Inventory.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", itemRequest.getOutletId())
                    .setParameter("itemId", itemRequest.getItemId())
                    .getResultList();
            Inventory inventory;
            if (invetorySearch.isEmpty()) {
                inventory = new Inventory();
                inventory.setIsActive(itemRequest.getIsActive());
                inventory.setItemId(itemRequest.getItemId());
                inventory.setLastUpdated(nowDate);
                inventory.setMerchantId(merchantId);
                inventory.setMinimumStock(new Long(0));
                inventory.setOutletId(itemRequest.getOutletId());
                inventory.setStock(new Long(0));
                inventory.setUpdatedBy(operatorId);
                em.persist(inventory);
            } else {
                inventory = invetorySearch.get(0);
                inventory.setIsActive(itemRequest.getIsActive());
                inventory.setLastUpdated(nowDate);
                inventory.setUpdatedBy(operatorId);
                em.merge(inventory);
            }
        }

        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setClientDateTime(nowDate);
        inventoryLog.setDateTime(nowDate);
        inventoryLog.setDescription(" ");
        inventoryLog.setMerchantId(merchantId);
        inventoryLog.setOperatorId(operatorId);
        inventoryLog.setOutletId(null);
        inventoryLog.setType(InventoryLogTypeEnum.ITEM_SETTING_CHANGE);
        em.persist(inventoryLog);
        em.flush();

        for (ItemRequestHolder itemRequest : listItemRequest) {
            InventoryLogDetail inventoryLogDetail = new InventoryLogDetail();
            inventoryLogDetail.setInventoryLogId(Long.parseLong(inventoryLog.getId()));
            inventoryLogDetail.setDescription(String.valueOf(itemRequest.getIsActive()));
            inventoryLogDetail.setItemId(itemRequest.getItemId());
            inventoryLogDetail.setItemName(itemRequest.getItemName());
            inventoryLogDetail.setItemQuantity(new Long(0));
            inventoryLogDetail.setOutletId(itemRequest.getOutletId());
            em.persist(inventoryLogDetail);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;

    }

    private JSONObject validateAll(String merchantCode, String userName, String items) {
        JSONObject response = new JSONObject();

        List<ItemRequestHolder> listItemRequest;

        try {
            listItemRequest = parseItemsRequest(items);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        JSONObject validateMerchantAndOperatorResponse = validateMerchantAndOperator(merchantCode, userName);
        if (!validateMerchantAndOperatorResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateMerchantAndOperatorResponse;
        }

        Long merchantId = validateMerchantAndOperatorResponse.getLong(MERCHANT_ID_KEY);
        Long operatorId = validateMerchantAndOperatorResponse.getLong(OPERATOR_ID_KEY);

        JSONObject validateItemAndOutletResponse = validateItemsAndOutlet(merchantId, listItemRequest);
        if (!validateItemAndOutletResponse.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateItemAndOutletResponse;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(LIST_ITEM_REQUEST_KEY, listItemRequest);
        response.put(MERCHANT_ID_KEY, merchantId);
        response.put(OPERATOR_ID_KEY, operatorId);
        return response;

    }

    private List<ItemRequestHolder> parseItemsRequest(String itemRequest) {
        JSONArray itemRequestArray = new JSONArray(itemRequest);
        Type listTypeItemRequest = new TypeToken<List<ItemRequestHolder>>() {
        }.getType();
        List<ItemRequestHolder> listItemRequest = new Gson().fromJson(itemRequestArray.toString(),
                listTypeItemRequest);
        return listItemRequest;
    }

    private JSONObject validateItemsAndOutlet(Long merchantId, List<ItemRequestHolder> listItemRequest) {
        JSONObject response = new JSONObject();

        Set<Long> setItemId = new HashSet<>();
        Set<Long> setOutletId = new HashSet<>();
        List<Long> listItemId = new ArrayList<>();
        List<Long> listOutletId = new ArrayList<>();
        for (ItemRequestHolder itemRequest : listItemRequest) {
            setItemId.add(itemRequest.getItemId());
            setOutletId.add(itemRequest.getOutletId());
        }
        listItemId.addAll(setItemId);
        listOutletId.addAll(setOutletId);

        List<Items> itemSearch = em.createQuery("SELECT i FROM Items i WHERE "
                + "i.merchantId = :merchantId AND i.id IN :ids", Items.class)
                .setParameter("merchantId", merchantId)
                .setParameter("ids", listItemId)
                .getResultList();
        if (itemSearch.size() < listItemId.size()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_ITEM_ID.getResponseStatus());
            return response;
        }

        List<Outlet> outletSearch = em.createQuery("SELECT o FROM Outlet o WHERE "
                + "o.merchantId = :merchantId AND o.id IN :ids", Outlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("ids", listOutletId)
                .getResultList();
        if (outletSearch.size() < listOutletId.size()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_OUTLET_ID.getResponseStatus());
            return response;
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
        response.put(MERCHANT_ID_KEY, merchantId);
        response.put(OPERATOR_ID_KEY, operatorId);
        return response;
    }

}

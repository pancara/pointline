/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.inventory;

import id.hardana.ejb.mpos.holder.InventoryItemHolder;
import id.hardana.ejb.mpos.holder.ItemRequestHolder;
import id.hardana.entity.inventory.Inventory;
import id.hardana.entity.inventory.InventoryLog;
import id.hardana.entity.inventory.InventoryLogDetail;
import id.hardana.entity.inventory.InventoryMerchant;
import id.hardana.entity.inventory.InventoryOutlet;
import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean

public class InventoryBean {

    private final String STATUS_KEY = "status";
    private final String MERCHANT_ID_KEY = "merchantId";
    private final String OPERATOR_ID_KEY = "operatorId";
    private final String DATA_KEY = "data";
    private final String IS_ACTIVE_MERCHANT_KEY = "isActiveMerchantInventory";
    private final String IS_ACTIVE_OUTLET_KEY = "isActiveOutletInventory";
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
                + "id.hardana.ejb.mpos.holder.InventoryItemHolder"
                + "(ot.id, ot.name, it.id, it.name, i.stock, i.minimumStock, o.userName, "
                + "i.lastUpdated, i.isActive) FROM Inventory i JOIN Outlet ot ON ot.id = i.outletId "
                + "JOIN Operator o ON o.id = i.updatedBy JOIN Items it ON i.itemId = it.id "
                + "WHERE i.merchantId = :merchantId AND i.outletId = :outletId",
                InventoryItemHolder.class)
                .setParameter("merchantId", merchantId)
                .setParameter("outletId", outletIdLong)
                .getResultList();

        Boolean isActiveMerchant = isActiveMerchantInventory(merchantId);
        Boolean isActiveOutlet = isActiveOutletInventory(merchantId, outletIdLong);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(IS_ACTIVE_MERCHANT_KEY, isActiveMerchant);
        response.put(IS_ACTIVE_OUTLET_KEY, isActiveOutlet);
        response.put(DATA_KEY, itemInventorySearch);
        return response;

    }

    public void changeStock(Long merchantId, Long outletId, Long operatorId, List<ItemRequestHolder> listItem,
            InventoryLogTypeEnum invetoryLogType, Long invoiceId, Date clientDateTime) {
        Date nowDate = new Date();

        Boolean isActiveMerchant = isActiveMerchantInventory(merchantId);
        Boolean isActiveOutlet = isActiveOutletInventory(merchantId, outletId);

        if (isActiveMerchant && isActiveOutlet) {

            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setClientDateTime(clientDateTime);
            inventoryLog.setDateTime(nowDate);
            inventoryLog.setDescription(" ");
            inventoryLog.setMerchantId(merchantId);
            inventoryLog.setOperatorId(operatorId);
            inventoryLog.setOutletId(outletId);
            inventoryLog.setType(invetoryLogType);
            inventoryLog.setInvoiceId(invoiceId);
            em.persist(inventoryLog);
            em.flush();

            for (ItemRequestHolder itemStock : listItem) {
                List<Inventory> invetorySearch = em.createQuery("SELECT i FROM Inventory i "
                        + "WHERE i.merchantId = :merchantId AND i.outletId = :outletId AND i.itemId = :itemId",
                        Inventory.class)
                        .setParameter("merchantId", merchantId)
                        .setParameter("outletId", outletId)
                        .setParameter("itemId", itemStock.getItemId())
                        .getResultList();
                Inventory inventory;
                if (!invetorySearch.isEmpty()) {
                    inventory = invetorySearch.get(0);
                    em.refresh(inventory);

                    if (Boolean.valueOf(inventory.getIsActive())) {
                        InventoryLogDetail inventoryLogDetail = new InventoryLogDetail();
                        inventoryLogDetail.setInventoryLogId(Long.parseLong(inventoryLog.getId()));
                        inventoryLogDetail.setDescription(" ");
                        inventoryLogDetail.setItemId(itemStock.getItemId());
                        inventoryLogDetail.setItemName(itemStock.getItemName());
                        inventoryLogDetail.setOutletId(outletId);

                        inventory.setLastUpdated(nowDate);
                        inventory.setUpdatedBy(operatorId);

                        if (invetoryLogType.equals(InventoryLogTypeEnum.INVOICE)) {
                            inventory.setStock(Long.parseLong(inventory.getStock()) - itemStock.getStock());
                            inventoryLogDetail.setItemQuantity(-itemStock.getStock());
                        } else {
                            inventory.setStock(Long.parseLong(inventory.getStock()) + itemStock.getStock());
                            inventoryLogDetail.setItemQuantity(itemStock.getStock());
                        }

                        em.merge(inventory);
                        em.persist(inventoryLogDetail);
                    }
                }
            }
        }

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

    private Boolean isActiveMerchantInventory(Long merchantId) {
        Boolean isActive = false;
        List<InventoryMerchant> inventoryMerchantSearch = em.createQuery("SELECT i FROM InventoryMerchant i "
                + "WHERE i.merchantId = :merchantId", InventoryMerchant.class)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (!inventoryMerchantSearch.isEmpty()) {
            InventoryMerchant inventoryMerchant = inventoryMerchantSearch.get(0);
            em.refresh(inventoryMerchant);
            isActive = Boolean.valueOf(inventoryMerchant.getIsActive());
        }

        return isActive;
    }

    private Boolean isActiveOutletInventory(Long merchantId, Long outletId) {
        Boolean isActive = false;

        List<InventoryOutlet> outletSettingSearch = em.createQuery("SELECT i FROM InventoryOutlet i "
                + "WHERE i.merchantId = :merchantId AND i.outletId = :outletId",
                InventoryOutlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("outletId", outletId)
                .getResultList();

        if (!outletSettingSearch.isEmpty()) {
            InventoryOutlet inventoryOutlet = outletSettingSearch.get(0);
            em.refresh(inventoryOutlet);
            isActive = Boolean.valueOf(inventoryOutlet.getIsActive());
        }

        return isActive;
    }

}

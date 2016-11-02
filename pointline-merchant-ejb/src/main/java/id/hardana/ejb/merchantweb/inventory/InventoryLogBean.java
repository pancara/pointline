/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory;

import id.hardana.ejb.merchantweb.inventory.extension.InventoryLogDetailHolder;
import id.hardana.ejb.merchantweb.inventory.extension.InventoryLogHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class InventoryLogBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String LOG_KEY = "log";
    private final String COUNT_KEY = "count";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject inventoryLog(String merchantCode, String limit, String page,
            String startDateString, String endDateString) {
        JSONObject response = new JSONObject();

        Integer limitInteger;
        Integer pageInteger;
        Date startDate;
        Date endDate;
        try {
            limitInteger = Integer.parseInt(limit);
            pageInteger = Integer.parseInt(page);
            startDate = DATE_FORMAT.parse(startDateString);
            endDate = DATE_FORMAT.parse(endDateString);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        if (pageInteger < 1) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        int firstResult = ((pageInteger - 1) * limitInteger);

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        List<InventoryLogTypeEnum> logTypes = new ArrayList<>();
        logTypes.add(InventoryLogTypeEnum.MERCHANT_SETTING_CHANGE);
        logTypes.add(InventoryLogTypeEnum.OUTLET_SETTING_CHANGE);
        logTypes.add(InventoryLogTypeEnum.ITEM_SETTING_CHANGE);
        logTypes.add(InventoryLogTypeEnum.CHANGE_MINIMUM_STOCK);
        logTypes.add(InventoryLogTypeEnum.RESTOCK);

        String queryInventoryLogCount = "SELECT COUNT(i.id) FROM InventoryLog i "
                + "WHERE i.merchantId = :merchantId AND i.type IN :types AND i.dateTime BETWEEN :startDate AND :endDate";
        Long inventoryLogCount = em.createQuery(queryInventoryLogCount, Long.class)
                .setParameter("merchantId", merchantId)
                .setParameter("types", logTypes)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        List<InventoryLogHolder> listInventoryLog = null;
        String queryInventoryLog = "SELECT NEW id.hardana.ejb.merchantweb.inventory.extension.InventoryLogHolder"
                + "(i.id, i.type, ot.name, o.userName, i.dateTime, i.clientDateTime, i.description) "
                + "FROM InventoryLog i LEFT JOIN Operator o ON i.operatorId = o.id "
                + "LEFT JOIN Outlet ot ON ot.id = i.outletId WHERE i.merchantId = :merchantId AND i.type IN :types "
                + "AND i.dateTime BETWEEN :startDate AND :endDate ORDER BY i.id DESC";

        if (inventoryLogCount > 0) {
            listInventoryLog = em.createQuery(queryInventoryLog, InventoryLogHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("types", logTypes)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .setFirstResult(firstResult)
                    .setMaxResults(limitInteger)
                    .getResultList();

            for (InventoryLogHolder inventoryLog : listInventoryLog) {
                if (inventoryLog.getType().equals(InventoryLogTypeEnum.INVOICE)
                        || inventoryLog.getType().equals(InventoryLogTypeEnum.CHANGE_MINIMUM_STOCK)
                        || inventoryLog.getType().equals(InventoryLogTypeEnum.RESTOCK)
                        || inventoryLog.getType().equals(InventoryLogTypeEnum.ITEM_SETTING_CHANGE)
                        || inventoryLog.getType().equals(InventoryLogTypeEnum.VOID_INVOICE)) {
                    String queryInvetoryLogDetail = "SELECT NEW id.hardana.ejb.merchantweb.inventory.extension.InventoryLogDetailHolder"
                            + "(i.outletId, ot.name, i.itemId, i.itemName, i.itemQuantity, i.description) "
                            + "FROM InventoryLogDetail i LEFT JOIN Outlet ot ON ot.id = i.outletId WHERE i.inventoryLogId = :inventoryLogId";
                    List<InventoryLogDetailHolder> listInventoryLogDetail = em.createQuery(queryInvetoryLogDetail, InventoryLogDetailHolder.class)
                            .setParameter("inventoryLogId", inventoryLog.getId())
                            .getResultList();
                    inventoryLog.setInventoryLogDetailHolder(listInventoryLogDetail);
                }
            }
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(COUNT_KEY, inventoryLogCount);
        response.put(LOG_KEY, listInventoryLog);
        return response;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.history;

import id.hardana.ejb.mpos.holder.TransactionMerchantTopupHolder;
import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class TopupHistoryBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final int HISTORY_NEW_VALUE = 0;
    private final int HISTORY_BEFORE_VALUE = -1;
    private final int HISTORY_AFTER_VALUE = 1;
    private final String STATUS_KEY = "status";
    private final String HISTORY_KEY = "history";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject topupHistoryNow(String merchantCode, String outletId,
            String limit) {
        return topupHistory(merchantCode, outletId, limit, null, HISTORY_NEW_VALUE);
    }

    public JSONObject topupHistoryBefore(String merchantCode, String outletId,
            String limit, String dateTime) {
        return topupHistory(merchantCode, outletId, limit, dateTime, HISTORY_BEFORE_VALUE);
    }

    public JSONObject topupHistoryAfter(String merchantCode, String outletId,
            String limit, String dateTime) {
        return topupHistory(merchantCode, outletId, limit, dateTime, HISTORY_AFTER_VALUE);
    }

    private JSONObject topupHistory(String merchantCode, String outletId,
            String limit, String dateTime, int historyValue) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        Integer limitInteger;
        try {
            outletIdLong = Long.parseLong(outletId);
            limitInteger = Integer.parseInt(limit);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        Date invoiceDateTime = null;
        if (historyValue != HISTORY_NEW_VALUE) {
            try {
                invoiceDateTime = DATE_FORMAT.parse(dateTime);
            } catch (ParseException ex) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
                return response;
            }
        }

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        List<Outlet> listOutlet = em.createQuery("SELECT o FROM Outlet o WHERE o.merchantId = :merchantId "
                + "AND o.status = :status AND o.isDeleted = :isDeleted AND o.id = :id", Outlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", OutletStatusEnum.ACTIVE)
                .setParameter("isDeleted", false)
                .setParameter("id", outletIdLong)
                .getResultList();
        if (listOutlet.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_OUTLET_ID.getResponseStatus());
            return response;
        }

        List<TransactionMerchantTopupHolder> listTransactionTopup = null;
        String queryTopupHistory = "SELECT NEW id.hardana.ejb.mpos.holder.TransactionMerchantTopupHolder"
                + "(t.referenceNumber, t.clientTransRefnum, t.amount, t.fee, "
                + "t.totalAmount, t.status, t.dateTime, tm.type, o.userName, c.pan, c.cardHolderName, "
                + "p.account, p.firstName, p.lastName) FROM TransactionMerchantTopup tm "
                + "JOIN TransactionTbl t ON tm.transactionId = t.id "
                + "JOIN Operator o ON tm.operatorId = o.id "
                + "LEFT JOIN Card c ON tm.topupDestination = c.id "
                + "LEFT JOIN PersonalInfo p ON tm.topupDestination = p.id "
                + "WHERE tm.merchantId = :merchantId AND tm.outletId = :outletId AND t.status = :status ";

        if (historyValue == HISTORY_NEW_VALUE) {
            queryTopupHistory += "ORDER BY t.id DESC";
            listTransactionTopup = em.createQuery(queryTopupHistory, TransactionMerchantTopupHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("status", ResponseStatusEnum.SUCCESS)
                    .setMaxResults(limitInteger)
                    .getResultList();
        } else if (historyValue == HISTORY_BEFORE_VALUE) {
            queryTopupHistory += "AND t.dateTime < :dateTime  ORDER BY t.id DESC";
            listTransactionTopup = em.createQuery(queryTopupHistory, TransactionMerchantTopupHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("dateTime", invoiceDateTime)
                    .setParameter("status", ResponseStatusEnum.SUCCESS)
                    .setMaxResults(limitInteger)
                    .getResultList();
        } else if (historyValue == HISTORY_AFTER_VALUE) {
            queryTopupHistory += "AND t.dateTime > :dateTime  ORDER BY t.id DESC";
            listTransactionTopup = em.createQuery(queryTopupHistory, TransactionMerchantTopupHolder.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("outletId", outletIdLong)
                    .setParameter("dateTime", invoiceDateTime)
                    .setParameter("status", ResponseStatusEnum.SUCCESS)
                    .setMaxResults(limitInteger)
                    .getResultList();
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(HISTORY_KEY, listTransactionTopup);
        return response;
    }

}

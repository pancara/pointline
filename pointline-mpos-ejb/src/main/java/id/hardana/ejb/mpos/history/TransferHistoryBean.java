/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.history;

import id.hardana.ejb.mpos.holder.TransactionTransferCardHistoryHolder;
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
import javax.persistence.Query;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class TransferHistoryBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final int HISTORY_NEW_VALUE = 0;
    private final int HISTORY_BEFORE_VALUE = -1;
    private final int HISTORY_AFTER_VALUE = 1;
    private final String STATUS_KEY = "status";
    private final String HISTORY_KEY = "history";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject transferHistoryNow(String merchantCode, String outletId,
            String limit) {
        return transferHistory(merchantCode, outletId, limit, null, HISTORY_NEW_VALUE);
    }

    public JSONObject transferHistoryBefore(String merchantCode, String outletId,
            String limit, String dateTime) {
        return transferHistory(merchantCode, outletId, limit, dateTime, HISTORY_BEFORE_VALUE);
    }

    public JSONObject transferHistoryAfter(String merchantCode, String outletId,
            String limit, String dateTime) {
        return transferHistory(merchantCode, outletId, limit, dateTime, HISTORY_AFTER_VALUE);
    }

    private JSONObject transferHistory(String merchantCode, String outletId,
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

        Date completionDateTime = null;
        if (historyValue != HISTORY_NEW_VALUE) {
            try {
                completionDateTime = DATE_FORMAT.parse(dateTime);
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

        List<TransactionTransferCardHistoryHolder> listTransactionTransfer = null;
        String queryTransferString = "SELECT NEW id.hardana.ejb.mpos.holder.TransactionTransferCardHistoryHolder"
                + "(t.id, t.referenceNumber, t.amount, t.status, p.account, p.firstName, c.pan, "
                + "c.cardHolderName, m.brandName, o.name, op.userName, ttpt.completionDateTime) "
                + "FROM TransactionTbl t JOIN TransactionTransfer tt ON tt.transactionId = t.id "
                + "JOIN TransactionTransferPersonalToCard ttpt ON ttpt.transactionTransferId = tt.id "
                + "JOIN PersonalInfo p ON tt.fromId = p.id JOIN Card c ON tt.toId = c.id "
                + "JOIN Merchant m ON ttpt.merchantId = m.id JOIN Outlet o ON ttpt.outletId = o.id "
                + "JOIN Operator op ON ttpt.operatorId = op.id "
                + "WHERE ttpt.merchantId = :merchantId AND ttpt.outletId = :outletId AND t.status = :status ";

        if (historyValue == HISTORY_NEW_VALUE) {
            queryTransferString += "ORDER BY ttpt.completionDateTime DESC";
        } else if (historyValue == HISTORY_BEFORE_VALUE) {
            queryTransferString += "AND ttpt.completionDateTime < :dateTime  ORDER BY ttpt.completionDateTime DESC";
        } else if (historyValue == HISTORY_AFTER_VALUE) {
            queryTransferString += "AND ttpt.completionDateTime > :dateTime  ORDER BY ttpt.completionDateTime DESC";
        }

        Query queryTransfer = em.createQuery(queryTransferString, TransactionTransferCardHistoryHolder.class);
        queryTransfer.setParameter("merchantId", merchantId);
        queryTransfer.setParameter("outletId", outletIdLong);
        queryTransfer.setParameter("status", ResponseStatusEnum.SUCCESS);
        queryTransfer.setMaxResults(limitInteger);

        if (historyValue != HISTORY_NEW_VALUE) {
            queryTransfer.setParameter("dateTime", completionDateTime);
        }
        
        listTransactionTransfer = queryTransfer.getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(HISTORY_KEY, listTransactionTransfer);
        return response;
    }

}

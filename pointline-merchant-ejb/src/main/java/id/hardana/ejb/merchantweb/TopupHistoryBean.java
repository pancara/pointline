/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import id.hardana.ejb.merchantweb.extension.TransactionMerchantTopupHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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
    private final String STATUS_KEY = "status";
    private final String HISTORY_KEY = "history";
    private final String COUNT_KEY = "count";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject topupHistory(String merchantCode, String limit, String page,
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

        String queryTopupHistoryCount = "SELECT COUNT(t.id) FROM TransactionMerchantTopup tm "
                + "JOIN TransactionTbl t ON tm.transactionId = t.id "
                + "WHERE tm.merchantId = :merchantId AND t.status = :status "
                + "AND t.dateTime BETWEEN :startDate AND :endDate ";
        
        Long topupHistoryCount = em.createQuery(queryTopupHistoryCount, Long.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", ResponseStatusEnum.SUCCESS)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        List<TransactionMerchantTopupHolder> listTransactionTopup = null;
        String queryTopupHistory = "SELECT NEW id.hardana.ejb.merchantweb.extension.TransactionMerchantTopupHolder"
                + "(t.referenceNumber, t.clientTransRefnum, t.amount, t.fee, "
                + "t.totalAmount, t.status, t.dateTime, tm.type, o.userName, c.pan, c.cardHolderName, "
                + "p.account, p.firstName, p.lastName, "
                + "ot.name, ot.latitude, ot.longitude) "
                + "FROM TransactionMerchantTopup tm "
                + "JOIN TransactionTbl t ON tm.transactionId = t.id "
                + "JOIN Operator o ON tm.operatorId = o.id "
                + "LEFT JOIN Card c ON tm.topupDestination = c.id "
                + "LEFT JOIN PersonalInfo p ON tm.topupDestination = p.id "
                + "LEFT JOIN Outlet ot ON ot.id = tm.outletId "
                + "WHERE tm.merchantId = :merchantId AND t.status = :status "
                + "AND t.dateTime BETWEEN :startDate AND :endDate ORDER BY t.id DESC";

        listTransactionTopup = em.createQuery(queryTopupHistory, TransactionMerchantTopupHolder.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", ResponseStatusEnum.SUCCESS)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setFirstResult(firstResult)
                .setMaxResults(limitInteger)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(HISTORY_KEY, listTransactionTopup);
        response.put(COUNT_KEY, topupHistoryCount);
        return response;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.dashboard;

import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.webpersonal.holder.InvoiceHolder;
import id.hardana.ejb.webpersonal.holder.InvoiceItemDiscountHolder;
import id.hardana.ejb.webpersonal.holder.InvoiceItemHolder;
import id.hardana.ejb.webpersonal.holder.InvoicePricingHolder;
import id.hardana.ejb.webpersonal.holder.InvoiceTransactionDiscountHolder;
import id.hardana.ejb.webpersonal.holder.TransactionPersonalHolder;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.entity.invoice.InvoiceItemDiscount;
import id.hardana.entity.invoice.InvoiceTransactionDiscount;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPersonalTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

public class TransactionHistoryPersonalBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String PERSONAL_HISTORY_KEY = "personalHistory";
    private final String COUNT_PERSONAL_KEY = "countPersonal";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject allPersonalHistoryByDate(String account, String limit, String page,
            String startDateString, String endDateString) {
        JSONObject response = new JSONObject();
        Date startDate = null;
        Date endDate = null;
        if (startDate != null && endDate != null) {
            try {
                startDate = DATE_FORMAT.parse(startDateString);
                endDate = DATE_FORMAT.parse(endDateString);
            } catch (ParseException ex) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
                return response;
            }
        }

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        Integer pageInteger;
        Integer limitInteger;
        try {
            limitInteger = Integer.parseInt(limit);
            pageInteger = Integer.parseInt(page);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        if (pageInteger < 1) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        JSONObject responsePersonalHistory = allPersonalHistoryProcess(formattedAccount, limitInteger,
                pageInteger, startDate, endDate);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PERSONAL_HISTORY_KEY, responsePersonalHistory.get(PERSONAL_HISTORY_KEY));
        response.put(COUNT_PERSONAL_KEY, responsePersonalHistory.get(COUNT_PERSONAL_KEY));
        return response;
    }

    private JSONObject allPersonalHistoryProcess(String formattedAccount, int limitInteger,
            int pageInteger, Date startDate, Date endDate) {
        JSONObject response = new JSONObject();
        List<TransactionPersonalHolder> listPersonalTransaction = new ArrayList<>();

        int firstResult = ((pageInteger - 1) * limitInteger);

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount",
                PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(PERSONAL_HISTORY_KEY, listPersonalTransaction);
            response.put(COUNT_PERSONAL_KEY, String.valueOf(0));
            return response;
        }
        Long personalInfoId = Long.parseLong(personalInfoSearch.get(0).getId());
        List<ResponseStatusEnum> statusList = new ArrayList<>();
        statusList.add(ResponseStatusEnum.SUCCESS);
        statusList.add(ResponseStatusEnum.QUEUEING);

        String debetCase = "(t.type = :typePersonalTopup AND tpt.type = :typeVAReversal "
                + "AND tpt.personalId = :personalInfoId) "
                + "OR (t.type = :typeTransfer AND tt.type = :typePTC AND tt.fromId = :personalInfoId) "
                + "OR (t.type = :typeTransfer AND tt.type = :typePTP AND tt.fromId = :personalInfoId) "
                + "OR (t.type = :typePayment AND tp.sourceId = :personalInfoId AND tp.type = :personalPaymentType) ";
        String creditCase = "(t.type = :typePersonalTopup AND tpt.type = :typeVA "
                + "AND tpt.personalId = :personalInfoId) "
                + "OR (t.type = :typeTransfer AND tt.type = :typePTP AND tt.toId = :personalInfoId) ";
        String dateLimit = "AND t.dateTime BETWEEN :startDate AND :endDate ";
        String orderBy = "ORDER BY t.id DESC ";

        String queryTransactionCount = "SELECT COUNT(t.id) FROM TransactionTbl t "
                + "LEFT JOIN TransactionPersonalTopup tpt ON t.id = tpt.transactionId "
                + "LEFT JOIN TransactionTransfer tt ON t.id = tt.transactionId "
                + "LEFT JOIN TransactionPayment tp ON t.id = tp.transactionId "
                + "WHERE (" + debetCase + " OR " + creditCase + ") AND t.status IN :listStatus ";
        if (startDate != null && endDate != null) {
            queryTransactionCount += dateLimit;
        }

        Query transactionCountQuery = em.createQuery(queryTransactionCount, Long.class);
        transactionCountQuery.setParameter("personalInfoId", personalInfoId);
        transactionCountQuery.setParameter("typePersonalTopup", TransactionTypeEnum.PERSONALTOPUP);
        transactionCountQuery.setParameter("typeVAReversal", TransactionPersonalTopupTypeEnum.VIRTUALACCOUNT_REVERSAL);
        transactionCountQuery.setParameter("typeVA", TransactionPersonalTopupTypeEnum.VIRTUALACCOUNT);
        transactionCountQuery.setParameter("typeTransfer", TransactionTypeEnum.TRANSFER);
        transactionCountQuery.setParameter("typePTC", TransactionTransferTypeEnum.PERSONALTOCARD);
        transactionCountQuery.setParameter("typePTP", TransactionTransferTypeEnum.PERSONALTOPERSONAL);
        transactionCountQuery.setParameter("typePayment", TransactionTypeEnum.PAYMENT);
        transactionCountQuery.setParameter("personalPaymentType", TransactionPaymentTypeEnum.PERSONAL);
        transactionCountQuery.setParameter("listStatus", statusList);
        Long transactionCount;

        if (startDate != null && endDate != null) {
            transactionCountQuery.setParameter("startDate", startDate);
            transactionCountQuery.setParameter("endDate", endDate);
        }

        transactionCount = (Long) transactionCountQuery.getSingleResult();

        if (transactionCount > 0) {
            String queryPersonalHistory = "SELECT NEW id.hardana.ejb.webpersonal.holder.TransactionPersonalHolder "
                    + "(t.id, t.referenceNumber, t.clientTransRefnum, "
                    + "t.type, t.amount, t.fee, t.totalAmount, t.status, t.dateTime, "
                    + "CASE WHEN " + debetCase + " THEN 'D' WHEN " + creditCase + " THEN 'C' ELSE 'D' END, "
                    + "tpt.type, tt.type, c.pan, c.cardHolderName, ttpc.type, ttpc.completionDateTime, "
                    + "m.brandName, o.name, o.latitude, o.longitude, "
                    + "tp.invoiceId, tp.type, p1.firstName, p1.lastName, p1.account, "
                    + "p2.firstName, p2.lastName, p2.account) "
                    + "FROM TransactionTbl t "
                    + "LEFT JOIN TransactionPersonalTopup tpt ON t.id = tpt.transactionId "
                    + "LEFT JOIN TransactionTransfer tt ON t.id = tt.transactionId "
                    + "LEFT JOIN TransactionPayment tp ON t.id = tp.transactionId "
                    + "LEFT JOIN Card c ON c.id = tt.toId "
                    + "LEFT JOIN PersonalInfo p1 ON p1.id = tt.fromId "
                    + "LEFT JOIN PersonalInfo p2 ON p2.id = tt.toId "
                    + "LEFT JOIN TransactionTransferPersonalToCard ttpc ON tt.id = ttpc.transactionTransferId "
                    + "LEFT JOIN Merchant m ON m.id = ttpc.merchantId "
                    + "LEFT JOIN Outlet o ON o.id = ttpc.outletId "
                    + "WHERE (" + debetCase + " OR " + creditCase + ") AND t.status IN :listStatus ";

            if (startDate == null || endDate == null) {
                queryPersonalHistory += orderBy;
            } else {
                queryPersonalHistory += dateLimit + orderBy;
            }

            Query personalHistoryQuery = em.createQuery(queryPersonalHistory, TransactionPersonalHolder.class);
            personalHistoryQuery.setParameter("personalInfoId", personalInfoId);
            personalHistoryQuery.setParameter("typePersonalTopup", TransactionTypeEnum.PERSONALTOPUP);
            personalHistoryQuery.setParameter("typeVAReversal", TransactionPersonalTopupTypeEnum.VIRTUALACCOUNT_REVERSAL);
            personalHistoryQuery.setParameter("typeVA", TransactionPersonalTopupTypeEnum.VIRTUALACCOUNT);
            personalHistoryQuery.setParameter("typeTransfer", TransactionTypeEnum.TRANSFER);
            personalHistoryQuery.setParameter("typePTC", TransactionTransferTypeEnum.PERSONALTOCARD);
            personalHistoryQuery.setParameter("typePTP", TransactionTransferTypeEnum.PERSONALTOPERSONAL);
            personalHistoryQuery.setParameter("typePayment", TransactionTypeEnum.PAYMENT);
            personalHistoryQuery.setParameter("personalPaymentType", TransactionPaymentTypeEnum.PERSONAL);
            personalHistoryQuery.setParameter("listStatus", statusList);

            if (startDate != null && endDate != null) {
                personalHistoryQuery.setParameter("startDate", startDate);
                personalHistoryQuery.setParameter("endDate", endDate);
            }

            listPersonalTransaction = personalHistoryQuery.setFirstResult(firstResult)
                    .setMaxResults(limitInteger)
                    .getResultList();

            for (TransactionPersonalHolder transactionPersonal : listPersonalTransaction) {
                if (transactionPersonal.getTransactionType().equals(TransactionTypeEnum.PAYMENT)) {
                    String queryInvoiceHistory = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoiceHolder"
                            + "(i.id, i.number, i.amount, i.dateTime, i.tableNumber, i.status, "
                            + "m.brandName, o.name, o.latitude, o.longitude) "
                            + "FROM Invoice i "
                            + "LEFT JOIN Merchant m ON m.id = i.merchantId "
                            + "LEFT JOIN Outlet o ON o.id = i.outletId "
                            + "WHERE i.id = :id";
                    try {
                        InvoiceHolder invoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                                .setParameter("id", transactionPersonal.getPaymentInvoiceId())
                                .getSingleResult();

                        String queryInvoiceItems = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoiceItemHolder"
                                + "(i.id, i.itemId, i.itemName, i.itemSupplyPrice, i.itemSalesPrice, i.itemQuantity, "
                                + "i.itemSubTotal) FROM InvoiceItems i WHERE i.invoiceId = :invoiceId";
                        List<InvoiceItemHolder> listItems = em.createQuery(queryInvoiceItems, InvoiceItemHolder.class)
                                .setParameter("invoiceId", invoice.getId())
                                .getResultList();

                        for (InvoiceItemHolder invoiceItemHolder : listItems) {
                            try {
                                String jql = "SELECT i FROM InvoiceItemDiscount i WHERE i.invoiceItemId = :invoiceItemId";
                                InvoiceItemDiscount itemDiscount = em.createQuery(jql, InvoiceItemDiscount.class)
                                        .setParameter("invoiceItemId", invoiceItemHolder.getId())
                                        .setMaxResults(1)
                                        .getSingleResult();

                                InvoiceItemDiscountHolder holder = new InvoiceItemDiscountHolder();

                                holder.setDiscountId(itemDiscount.getDiscountId());
                                holder.setDiscountName(itemDiscount.getDiscountName());

                                holder.setDiscountAmount(itemDiscount.getDiscountAmount());

                                holder.setDiscountApplyType(itemDiscount.getDiscountApplyType().getDiscountApplyTypeId());
                                holder.setDiscountCalculationType(itemDiscount.getDiscountCalculationType().getDiscountCalculationTypeId());

                                holder.setDiscountValue(itemDiscount.getDiscountValue());
                                holder.setDiscountValueType(itemDiscount.getDiscountValueType().getDiscountValueTypeId());

                                holder.setDiscountDescription(itemDiscount.getDiscountDescription());

                                invoiceItemHolder.setItemDiscount(holder);
                            } catch (NoResultException nre) {
                                // if no result just ignore.
                            }
                        }

                        invoice.setInvoiceItems(listItems);

                        // get transaction discount
                        String queryTrxDiscount = "SELECT trxd FROM InvoiceTransactionDiscount trxd WHERE trxd.invoiceId = :invoiceId";
                        try {
                            InvoiceTransactionDiscount trxDiscount = em.createQuery(queryTrxDiscount, InvoiceTransactionDiscount.class)
                                    .setParameter("invoiceId", invoice.getId())
                                    .setMaxResults(1)
                                    .getSingleResult();

                            InvoiceTransactionDiscountHolder trxDiscountHolder = new InvoiceTransactionDiscountHolder();
                            trxDiscountHolder.setDiscountId(trxDiscount.getDiscountId());
                            trxDiscountHolder.setDiscountName(trxDiscount.getDiscountName());
                            trxDiscountHolder.setDiscountDescription(trxDiscount.getDiscountDescription());

                            trxDiscountHolder.setDiscountValue(trxDiscount.getDiscountValue());
                            trxDiscountHolder.setDiscountValueType(trxDiscount.getDiscountValueType().getDiscountValueTypeId());
                            trxDiscountHolder.setDiscountAmount(trxDiscount.getDiscountAmount());

                            trxDiscountHolder.setDiscountApplyType(trxDiscount.getDiscountApplyType().getDiscountApplyTypeId());
                            trxDiscountHolder.setDiscountCalculationType(trxDiscount.getDiscountCalculationType().getDiscountCalculationTypeId());

                            invoice.setTransactionDiscount(trxDiscountHolder);
                        } catch (NoResultException nre) {

                        }

                        String queryInvoicePricing = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoicePricingHolder"
                                + "(p.name, i.pricingType, i.pricingValue, i.pricingAmount) "
                                + "FROM InvoicePricing i LEFT JOIN Pricing p ON i.pricingId = p.id "
                                + "WHERE i.invoiceId = :invoiceId";
                        List<InvoicePricingHolder> listPricing = em.createQuery(queryInvoicePricing, InvoicePricingHolder.class)
                                .setParameter("invoiceId", invoice.getId())
                                .getResultList();
                        invoice.setInvoicePricing(listPricing);

                        transactionPersonal.setPaymentInvoice(invoice);
                    } catch (Exception e) {
                    }
                }
            }

        }

        response.put(PERSONAL_HISTORY_KEY, listPersonalTransaction);
        response.put(COUNT_PERSONAL_KEY, String.valueOf(transactionCount));
        return response;

    }

}

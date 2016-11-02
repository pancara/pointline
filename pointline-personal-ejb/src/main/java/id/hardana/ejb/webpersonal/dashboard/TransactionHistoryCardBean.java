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
import id.hardana.ejb.webpersonal.holder.TransactionCardHolder;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.entity.invoice.InvoiceItemDiscount;
import id.hardana.entity.invoice.InvoiceTransactionDiscount;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
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

public class TransactionHistoryCardBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String CARD_HISTORY_KEY = "cardHistory";
    private final String COUNT_CARD_KEY = "countCard";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject allCardHistoryByDate(String account, String limit, String page,
            String startDateString, String endDateString) {
        JSONObject response = new JSONObject();
        Date startDate = null;
        Date endDate = null;
        if (startDateString != null && endDateString != null) {
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

        JSONObject responseCardHistory = allCardHistoryProcess(formattedAccount, limitInteger,
                pageInteger, startDate, endDate);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(CARD_HISTORY_KEY, responseCardHistory.get(CARD_HISTORY_KEY));
        response.put(COUNT_CARD_KEY, responseCardHistory.get(COUNT_CARD_KEY));
        return response;
    }

    private JSONObject allCardHistoryProcess(String formattedAccount, int limitInteger,
            int pageInteger, Date startDate, Date endDate) {
        JSONObject response = new JSONObject();
        List<TransactionCardHolder> listCardTransaction = new ArrayList<>();

        int firstResult = ((pageInteger - 1) * limitInteger);

        List<Card> cardIdSearch = em.createQuery("SELECT c FROM PersonalToCard pt "
                + "JOIN Card c ON pt.cardId = c.id "
                + "JOIN PersonalInfo p ON pt.personalInfoId = p.id "
                + "WHERE p.account = :account", Card.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (cardIdSearch.isEmpty()) {
            response.put(CARD_HISTORY_KEY, listCardTransaction);
            response.put(COUNT_CARD_KEY, String.valueOf(0));
            return response;
        }
        List<Long> cardIdList = new ArrayList<>();
        for (Card card : cardIdSearch) {
            cardIdList.add(Long.parseLong(card.getId()));
        }

        List<TransactionPaymentTypeEnum> cardPaymentTypeList = new ArrayList<>();
        cardPaymentTypeList.add(TransactionPaymentTypeEnum.CASHCARD);
        cardPaymentTypeList.add(TransactionPaymentTypeEnum.ADJUSTMENT_CASHCARD);

        List<TransactionPaymentTypeEnum> cardPaymentReversalTypeList = new ArrayList<>();
        cardPaymentReversalTypeList.add(TransactionPaymentTypeEnum.REVERSAL_CASH_CARD);

        List<TransactionMerchantTopupTypeEnum> topupDebitTypeList = new ArrayList<>();
        topupDebitTypeList.add(TransactionMerchantTopupTypeEnum.MANUAL_REVERSAL_CASHCARD);
        topupDebitTypeList.add(TransactionMerchantTopupTypeEnum.REVERSAL_CASHCARD);

        List<TransactionMerchantTopupTypeEnum> topupCreditTypeList = new ArrayList<>();
        topupCreditTypeList.add(TransactionMerchantTopupTypeEnum.CASHCARD);

        String debetCase = "((t.type = :typePayment) AND (tp.sourceId = c.id) AND (tp.type IN :cardPaymentTypeList)) "
                + "OR ((t.type = :typeTransfer) AND (tt.type = :typeCTP AND tt.fromId = c.id)) "
                + "OR ((t.type = :typeTopup) AND (tm.topupDestination = c.id) AND (tm.type IN :topupDebitTypeList)) ";
        String creditCase = "((t.type = :typeTopup) AND (tm.topupDestination = c.id) AND (tm.type IN :topupCreditTypeList)) "
                + "OR ((t.type = :typeTransfer) AND (tt.type = :typePTC) AND (tt.toId = c.id)) "
                + "OR ((t.type = :typePayment) AND (tp.sourceId = c.id) AND (tp.type IN :cardPaymentReversalTypeList)) ";
        String dateLimit = "AND t.dateTime BETWEEN :startDate AND :endDate ";
        String orderBy = "ORDER BY t.id DESC ";

        String queryTransactionCount = "SELECT COUNT(t.id) FROM TransactionTbl t "
                + "JOIN Card c LEFT JOIN TransactionPayment tp ON t.id = tp.transactionId "
                + "LEFT JOIN TransactionMerchantTopup tm ON t.id = tm.transactionId "
                + "LEFT JOIN TransactionTransfer tt ON t.id = tt.transactionId "
                + "WHERE c.id IN :ids AND (" + debetCase + " OR " + creditCase + ") AND t.status = :status ";
        if (startDate != null && endDate != null) {
            queryTransactionCount += dateLimit;
        }

        Query transactionCountQuery = em.createQuery(queryTransactionCount, Long.class);
        transactionCountQuery.setParameter("ids", cardIdList);
        transactionCountQuery.setParameter("typePayment", TransactionTypeEnum.PAYMENT);
        transactionCountQuery.setParameter("typeTopup", TransactionTypeEnum.MERCHANTTOPUP);
        transactionCountQuery.setParameter("typeTransfer", TransactionTypeEnum.TRANSFER);
        transactionCountQuery.setParameter("cardPaymentTypeList", cardPaymentTypeList);
        transactionCountQuery.setParameter("cardPaymentReversalTypeList", cardPaymentReversalTypeList);
        transactionCountQuery.setParameter("topupDebitTypeList", topupDebitTypeList);
        transactionCountQuery.setParameter("topupCreditTypeList", topupCreditTypeList);
        transactionCountQuery.setParameter("typePTC", TransactionTransferTypeEnum.PERSONALTOCARD);
        transactionCountQuery.setParameter("typeCTP", TransactionTransferTypeEnum.CARDTOPERSONAL);
        transactionCountQuery.setParameter("status", ResponseStatusEnum.SUCCESS);
        Long transactionCount;

        if (startDate != null && endDate != null) {
            transactionCountQuery.setParameter("startDate", startDate);
            transactionCountQuery.setParameter("endDate", endDate);
        }

        transactionCount = (Long) transactionCountQuery.getSingleResult();

        if (transactionCount > 0) {
            String queryCardHistory = "SELECT NEW id.hardana.ejb.webpersonal.holder.TransactionCardHolder "
                    + "(t.id, t.referenceNumber, t.clientTransRefnum, "
                    + "t.type, t.amount, t.fee, t.totalAmount, t.status, t.dateTime, "
                    + "CASE WHEN " + debetCase + " THEN 'D' WHEN " + creditCase + " THEN 'C' ELSE 'D' END, "
                    + "tp.invoiceId, m.brandName, o.name, o.latitude, o.longitude, "
                    + "tt.type, pi.account, pi.firstName, pi.lastName, "
                    + "po.account, po.firstName, po.lastName, c.pan, c.cardHolderName, "
                    + "tp.type, tm.type, t2.referenceNumber) "
                    + "FROM TransactionTbl t JOIN Card c "
                    + "LEFT JOIN TransactionPayment tp ON t.id = tp.transactionId "
                    + "LEFT JOIN TransactionMerchantTopup tm ON t.id = tm.transactionId "
                    + "LEFT JOIN Merchant m ON m.id = tm.merchantId "
                    + "LEFT JOIN Outlet o ON o.id = tm.outletId "
                    + "LEFT JOIN TransactionTransfer tt ON t.id = tt.transactionId "
                    + "LEFT JOIN PersonalInfo pi ON tt.fromId = pi.id "
                    + "LEFT JOIN PersonalInfo po ON tt.toId = po.id "
                    + "LEFT JOIN Invoice i ON i.id = tp.invoiceId "
                    + "LEFT JOIN InvoiceReversal ir ON ir.invoiceId = i.id "
                    + "LEFT JOIN Invoice i2 ON i2.id = ir.invoiceIdReference "
                    + "LEFT JOIN TransactionPayment tp2 ON tp2.invoiceId = i2.id "
                    + "LEFT JOIN TransactionTbl t2 ON t2.id = tp2.transactionId "
                    + "WHERE c.id IN :ids AND (" + debetCase + " OR " + creditCase + ") AND t.status = :status ";

            if (startDate == null || endDate == null) {
                queryCardHistory += orderBy;
            } else {
                queryCardHistory += dateLimit + orderBy;
            }

            Query cardHistoryQuery = em.createQuery(queryCardHistory, TransactionCardHolder.class);
            cardHistoryQuery.setParameter("ids", cardIdList);
            cardHistoryQuery.setParameter("typePayment", TransactionTypeEnum.PAYMENT);
            cardHistoryQuery.setParameter("typeTopup", TransactionTypeEnum.MERCHANTTOPUP);
            cardHistoryQuery.setParameter("typeTransfer", TransactionTypeEnum.TRANSFER);
            cardHistoryQuery.setParameter("cardPaymentTypeList", cardPaymentTypeList);
            cardHistoryQuery.setParameter("cardPaymentReversalTypeList", cardPaymentReversalTypeList);
            cardHistoryQuery.setParameter("topupDebitTypeList", topupDebitTypeList);
            cardHistoryQuery.setParameter("topupCreditTypeList", topupCreditTypeList);
            cardHistoryQuery.setParameter("typePTC", TransactionTransferTypeEnum.PERSONALTOCARD);
            cardHistoryQuery.setParameter("typeCTP", TransactionTransferTypeEnum.CARDTOPERSONAL);
            cardHistoryQuery.setParameter("status", ResponseStatusEnum.SUCCESS);

            if (startDate != null && endDate != null) {
                cardHistoryQuery.setParameter("startDate", startDate);
                cardHistoryQuery.setParameter("endDate", endDate);
            }

            listCardTransaction = cardHistoryQuery.setFirstResult(firstResult)
                    .setMaxResults(limitInteger)
                    .getResultList();

            for (TransactionCardHolder cardTransaction : listCardTransaction) {
                if (cardTransaction.getTransactionType().equals(TransactionTypeEnum.PAYMENT)) {
                    String queryInvoiceHistory = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoiceHolder"
                            + "(i.id, i.number, i.amount, i.dateTime, i.tableNumber, i.status, "
                            + "m.brandName, o.name, o.latitude, o.longitude) "
                            + "FROM Invoice i "
                            + "LEFT JOIN Merchant m ON m.id = i.merchantId "
                            + "LEFT JOIN Outlet o ON o.id = i.outletId "
                            + "WHERE i.id = :id";
                    try {
                        InvoiceHolder invoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                                .setParameter("id", Long.parseLong(cardTransaction.getPaymentInvoiceId()))
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

                        cardTransaction.setPaymentInvoice(invoice);
                    } catch (Exception e) {
                    }
                }
            }
        }

        response.put(CARD_HISTORY_KEY, listCardTransaction);
        response.put(COUNT_CARD_KEY, String.valueOf(transactionCount));
        return response;
    }

}

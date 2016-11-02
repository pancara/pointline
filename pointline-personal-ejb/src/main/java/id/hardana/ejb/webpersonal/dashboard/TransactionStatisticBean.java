/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.dashboard;

import com.google.gson.Gson;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.webpersonal.holder.InvoiceHolder;
import id.hardana.ejb.webpersonal.holder.InvoiceItemHolder;
import id.hardana.ejb.webpersonal.holder.InvoicePricingHolder;
import id.hardana.ejb.webpersonal.holder.TransactionCardHolder;
import id.hardana.ejb.webpersonal.holder.TransactionPersonalHolder;
import id.hardana.ejb.webpersonal.holder.TransactionStatisticHolder;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.sys.log.Login;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPersonalTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

public class TransactionStatisticBean {

    private final String STATUS_KEY = "status";
    private final String STATISTIC_KEY = "statistic";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject dailyStatistic(String account) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date startDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        return statistic(account, startDate, endDate);
    }

    public JSONObject monthlyStatistic(String account) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, -31);
        Date startDate = date.getTime();
        return statistic(account, startDate, endDate);
    }

    public JSONObject weekToDateStatistic(String account) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, -1);
        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            date.add(Calendar.DAY_OF_MONTH, -1);
        }
        date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = date.getTime();
        return statistic(account, startDate, endDate);
    }

    public JSONObject monthToDateStatistic(String account) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, -1);
        date.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = date.getTime();
        return statistic(account, startDate, endDate);
    }

    public JSONObject yearToDateStatistic(String account) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, -1);
        date.set(Calendar.DAY_OF_YEAR, 1);
        Date startDate = date.getTime();
        return statistic(account, startDate, endDate);
    }

    private JSONObject statistic(String account, Date startDate, Date endDate) {
        JSONObject response = new JSONObject();
        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        TransactionStatisticHolder cardStatistic = cardStatistic(account, startDate, endDate);
        TransactionStatisticHolder personalStatistic = personalStatistic(account, startDate, endDate);
        TransactionStatisticHolder transactionStatistic = augmentedStatistic(cardStatistic, personalStatistic);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(STATISTIC_KEY, new JSONObject(new Gson().toJson(transactionStatistic)));
        return response;
    }

    private TransactionStatisticHolder augmentedStatistic(TransactionStatisticHolder cardStat,
            TransactionStatisticHolder personalStat) {
        Long augTotalTransaction = Long.parseLong(cardStat.getTotalTransaction())
                + Long.parseLong(personalStat.getTotalTransaction());
        Long augDebetTransaction = Long.parseLong(cardStat.getDebetTransaction())
                + Long.parseLong(personalStat.getDebetTransaction());
        Long augCreditTransaction = Long.parseLong(cardStat.getCreditTransaction())
                + Long.parseLong(personalStat.getCreditTransaction());
        BigDecimal totalAmountCard = new BigDecimal(cardStat.getTotalAmount());
        BigDecimal totalAmountPersonal = new BigDecimal(personalStat.getTotalAmount());
        BigDecimal augTotalAmount = totalAmountCard.add(totalAmountPersonal);
        BigDecimal debetAmountCard = new BigDecimal(cardStat.getDebetAmount());
        BigDecimal debetAmountPersonal = new BigDecimal(personalStat.getDebetAmount());
        BigDecimal augDebetAmount = debetAmountCard.add(debetAmountPersonal);
        BigDecimal creditAmountCard = new BigDecimal(cardStat.getCreditAmount());
        BigDecimal creditAmountPersonal = new BigDecimal(personalStat.getCreditAmount());
        BigDecimal augCreditAmount = creditAmountCard.add(creditAmountPersonal);
        cardStat.setTotalTransaction(augTotalTransaction);
        cardStat.setDebetTransaction(augDebetTransaction);
        cardStat.setCreditTransaction(augCreditTransaction);
        cardStat.setTotalAmount(augTotalAmount);
        cardStat.setDebetAmount(augDebetAmount);
        cardStat.setCreditAmount(augCreditAmount);
        cardStat.setLastTransactionPersonal(personalStat.getLastTransactionPersonal());
        return cardStat;
    }

    private TransactionStatisticHolder personalStatistic(String account, Date startDate, Date endDate) {
        TransactionStatisticHolder personalStatistic = new TransactionStatisticHolder(new Long(0),
                new Long(0), new Long(0), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount",
                PersonalInfo.class)
                .setParameter("account", account)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            return personalStatistic;
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

        String queryStatistic = "SELECT NEW id.hardana.ejb.webpersonal.holder.TransactionStatisticHolder "
                + "(COUNT(t.id), SUM(CASE WHEN " + debetCase + " THEN 1 ELSE 0 END), "
                + "SUM(CASE WHEN " + creditCase + " THEN 1 ELSE 0 END), SUM(t.totalAmount),"
                + "SUM(CASE WHEN " + debetCase + " THEN t.totalAmount ELSE 0 END), "
                + "SUM(CASE WHEN " + creditCase + " THEN t.totalAmount ELSE 0 END)) "
                + "FROM TransactionTbl t "
                + "LEFT JOIN TransactionPersonalTopup tpt ON t.id = tpt.transactionId "
                + "LEFT JOIN TransactionTransfer tt ON t.id = tt.transactionId "
                + "LEFT JOIN TransactionPayment tp ON t.id = tp.transactionId "
                + "WHERE (" + debetCase + " OR " + creditCase + ") AND t.status IN :listStatus "
                + dateLimit;
        personalStatistic = em.createQuery(queryStatistic, TransactionStatisticHolder.class)
                .setParameter("personalInfoId", personalInfoId)
                .setParameter("typePersonalTopup", TransactionTypeEnum.PERSONALTOPUP)
                .setParameter("typeVAReversal", TransactionPersonalTopupTypeEnum.VIRTUALACCOUNT_REVERSAL)
                .setParameter("typeVA", TransactionPersonalTopupTypeEnum.VIRTUALACCOUNT)
                .setParameter("typeTransfer", TransactionTypeEnum.TRANSFER)
                .setParameter("typePTC", TransactionTransferTypeEnum.PERSONALTOCARD)
                .setParameter("typePTP", TransactionTransferTypeEnum.PERSONALTOPERSONAL)
                .setParameter("typePayment", TransactionTypeEnum.PAYMENT)
                .setParameter("personalPaymentType", TransactionPaymentTypeEnum.PERSONAL)
                .setParameter("listStatus", statusList)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        personalStatistic.setLastTransactionPersonal(personalLastTransaction(personalInfoId,
                debetCase, creditCase, statusList));

        return personalStatistic;
    }

    private TransactionStatisticHolder cardStatistic(String account, Date startDate, Date endDate) {
        TransactionStatisticHolder cardStatistic = new TransactionStatisticHolder(new Long(0), new Long(0),
                new Long(0), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        List<Card> cardIdSearch = em.createQuery("SELECT c FROM PersonalToCard pt "
                + "JOIN Card c ON pt.cardId = c.id "
                + "JOIN PersonalInfo p ON pt.personalInfoId = p.id "
                + "WHERE p.account = :account", Card.class)
                .setParameter("account", account)
                .getResultList();
        if (cardIdSearch.isEmpty()) {
            return cardStatistic;
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

        String debetCase = "(t.type = :typePayment AND tp.sourceId = c.id AND tp.type IN :cardPaymentTypeList) "
                + "OR (t.type = :typeTransfer AND tt.type = :typeCTP AND tt.fromId = c.id) "
                + "OR (t.type = :typeTopup AND tm.topupDestination = c.id AND tm.type IN :topupDebitTypeList) ";
        String creditCase = "(t.type = :typeTopup AND tm.topupDestination = c.id AND tm.type IN :topupCreditTypeList) "
                + "OR (t.type = :typeTransfer AND tt.type = :typePTC AND tt.toId = c.id) "
                + "OR ((t.type = :typePayment) AND (tp.sourceId = c.id) AND (tp.type IN :cardPaymentReversalTypeList)) ";
        String dateLimit = "AND t.dateTime BETWEEN :startDate AND :endDate ";

        String queryStatistic = "SELECT NEW id.hardana.ejb.webpersonal.holder.TransactionStatisticHolder "
                + "(COUNT(t.id), SUM(CASE WHEN " + debetCase + " THEN 1 ELSE 0 END), "
                + "SUM(CASE WHEN " + creditCase + " THEN 1 ELSE 0 END), SUM(t.totalAmount),"
                + "SUM(CASE WHEN " + debetCase + " THEN t.totalAmount ELSE 0 END), "
                + "SUM(CASE WHEN " + creditCase + " THEN t.totalAmount ELSE 0 END)) "
                + "FROM TransactionTbl t "
                + "JOIN Card c LEFT JOIN TransactionPayment tp ON t.id = tp.transactionId "
                + "LEFT JOIN TransactionMerchantTopup tm ON t.id = tm.transactionId "
                + "LEFT JOIN TransactionTransfer tt ON t.id = tt.transactionId "
                + "WHERE c.id IN :ids AND (" + debetCase + " OR " + creditCase + ") AND t.status = :status "
                + dateLimit;
        cardStatistic = em.createQuery(queryStatistic, TransactionStatisticHolder.class)
                .setParameter("ids", cardIdList)
                .setParameter("typePayment", TransactionTypeEnum.PAYMENT)
                .setParameter("typeTopup", TransactionTypeEnum.MERCHANTTOPUP)
                .setParameter("typeTransfer", TransactionTypeEnum.TRANSFER)
                .setParameter("cardPaymentTypeList", cardPaymentTypeList)
                .setParameter("cardPaymentReversalTypeList", cardPaymentReversalTypeList)
                .setParameter("topupDebitTypeList", topupDebitTypeList)
                .setParameter("topupCreditTypeList", topupCreditTypeList)
                .setParameter("typePTC", TransactionTransferTypeEnum.PERSONALTOCARD)
                .setParameter("typeCTP", TransactionTransferTypeEnum.CARDTOPERSONAL)
                .setParameter("status", ResponseStatusEnum.SUCCESS)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        cardStatistic.setLastTransactionCard(cardLastTransaction(cardIdList, debetCase, creditCase,
                cardPaymentTypeList, topupDebitTypeList, topupCreditTypeList, cardPaymentReversalTypeList));

        List<Login> loginList = em.createQuery("SELECT l FROM Login l "
                + "JOIN PersonalInfo p ON l.userId = p.id "
                + "WHERE l.merchantId is null AND p.account = :account "
                + "ORDER BY l.loginTime DESC", Login.class)
                .setParameter("account", account)
                .getResultList();
        if (loginList.size() > 1) {
            Login login = loginList.get(1);
            cardStatistic.setLastLogin(login.getLoginTime());
            String logoutTime = login.getLogoutTime();
            if (logoutTime == null) {
                if (loginList.size() > 2) {
                    Login login2 = loginList.get(2);
                    cardStatistic.setLastLogout(login2.getLogoutTime());
                }
            } else {
                cardStatistic.setLastLogout(login.getLogoutTime());
            }
        }

        return cardStatistic;
    }

    private TransactionCardHolder cardLastTransaction(List<Long> cardIdList, String debetCase, String creditCase,
            List<TransactionPaymentTypeEnum> cardPaymentTypeList, List<TransactionMerchantTopupTypeEnum> topupDebitTypeList,
            List<TransactionMerchantTopupTypeEnum> topupCreditTypeList, List<TransactionPaymentTypeEnum> cardPaymentReversalTypeList) {
        String orderBy = "ORDER BY t.id DESC ";
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
                + "WHERE c.id IN :ids AND (" + debetCase + " OR " + creditCase + ") AND t.status = :status "
                + orderBy;
        List<TransactionCardHolder> cardTransactionList = em.createQuery(queryCardHistory, TransactionCardHolder.class)
                .setParameter("ids", cardIdList)
                .setParameter("typePayment", TransactionTypeEnum.PAYMENT)
                .setParameter("typeTopup", TransactionTypeEnum.MERCHANTTOPUP)
                .setParameter("typeTransfer", TransactionTypeEnum.TRANSFER)
                .setParameter("cardPaymentTypeList", cardPaymentTypeList)
                .setParameter("cardPaymentReversalTypeList", cardPaymentReversalTypeList)
                .setParameter("topupDebitTypeList", topupDebitTypeList)
                .setParameter("topupCreditTypeList", topupCreditTypeList)
                .setParameter("typePTC", TransactionTransferTypeEnum.PERSONALTOCARD)
                .setParameter("typeCTP", TransactionTransferTypeEnum.CARDTOPERSONAL)
                .setParameter("status", ResponseStatusEnum.SUCCESS)
                .getResultList();

        if (!cardTransactionList.isEmpty()) {
            TransactionCardHolder cardTransaction = cardTransactionList.get(0);

            try {
                if (cardTransaction.getTransactionType().equals(TransactionTypeEnum.PAYMENT)) {
                    String queryInvoiceHistory = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoiceHolder"
                            + "(i.id, i.number, i.amount, i.dateTime, i.tableNumber, i.status, "
                            + "m.brandName, o.name, o.latitude, o.longitude) "
                            + "FROM Invoice i "
                            + "LEFT JOIN Merchant m ON m.id = i.merchantId "
                            + "LEFT JOIN Outlet o ON o.id = i.outletId "
                            + "WHERE i.id = :id";
                    InvoiceHolder invoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                            .setParameter("id", Long.parseLong(cardTransaction.getPaymentInvoiceId()))
                            .getSingleResult();

                    String queryInvoiceItems = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoiceItemHolder"
                            + "(i.itemName, i.itemSupplyPrice, i.itemSalesPrice, i.itemQuantity, "
                            + "i.itemSubTotal) FROM InvoiceItems i WHERE i.invoiceId = :invoiceId";
                    List<InvoiceItemHolder> listItems = em.createQuery(queryInvoiceItems, InvoiceItemHolder.class)
                            .setParameter("invoiceId", invoice.getId())
                            .getResultList();
                    invoice.setInvoiceItems(listItems);

                    String queryInvoicePricing = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoicePricingHolder"
                            + "(p.name, i.pricingType, i.pricingValue, i.pricingAmount) "
                            + "FROM InvoicePricing i LEFT JOIN Pricing p ON i.pricingId = p.id "
                            + "WHERE i.invoiceId = :invoiceId";
                    List<InvoicePricingHolder> listPricing = em.createQuery(queryInvoicePricing, InvoicePricingHolder.class)
                            .setParameter("invoiceId", invoice.getId())
                            .getResultList();
                    invoice.setInvoicePricing(listPricing);

                    cardTransaction.setPaymentInvoice(invoice);
                }
            } catch (Exception e) {
            }
            return cardTransaction;
        }
        return null;
    }

    private TransactionPersonalHolder personalLastTransaction(Long personalInfoId,
            String debetCase, String creditCase, List<ResponseStatusEnum> statusList) {
        String orderBy = "ORDER BY t.id DESC ";
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
                + "WHERE (" + debetCase + " OR " + creditCase + ") AND t.status IN :listStatus "
                + orderBy;

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

        List<TransactionPersonalHolder> personalTransactionList = personalHistoryQuery.getResultList();
        if (!personalTransactionList.isEmpty()) {
            TransactionPersonalHolder personalTransaction = personalTransactionList.get(0);
            if (personalTransaction.getTransactionType().equals(TransactionTypeEnum.PAYMENT)) {
                String queryInvoiceHistory = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoiceHolder"
                        + "(i.id, i.number, i.amount, i.dateTime, i.tableNumber, i.status, "
                        + "m.brandName, o.name, o.latitude, o.longitude) "
                        + "FROM Invoice i "
                        + "LEFT JOIN Merchant m ON m.id = i.merchantId "
                        + "LEFT JOIN Outlet o ON o.id = i.outletId "
                        + "WHERE i.id = :id";
                try {
                    InvoiceHolder invoice = em.createQuery(queryInvoiceHistory, InvoiceHolder.class)
                            .setParameter("id", personalTransaction.getPaymentInvoiceId())
                            .getSingleResult();

                    String queryInvoiceItems = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoiceItemHolder"
                            + "(i.itemName, i.itemSupplyPrice, i.itemSalesPrice, i.itemQuantity, "
                            + "i.itemSubTotal) FROM InvoiceItems i WHERE i.invoiceId = :invoiceId";
                    List<InvoiceItemHolder> listItems = em.createQuery(queryInvoiceItems, InvoiceItemHolder.class)
                            .setParameter("invoiceId", invoice.getId())
                            .getResultList();
                    invoice.setInvoiceItems(listItems);

                    String queryInvoicePricing = "SELECT NEW id.hardana.ejb.webpersonal.holder.InvoicePricingHolder"
                            + "(p.name, i.pricingType, i.pricingValue, i.pricingAmount) "
                            + "FROM InvoicePricing i LEFT JOIN Pricing p ON i.pricingId = p.id "
                            + "WHERE i.invoiceId = :invoiceId";
                    List<InvoicePricingHolder> listPricing = em.createQuery(queryInvoicePricing, InvoicePricingHolder.class)
                            .setParameter("invoiceId", invoice.getId())
                            .getResultList();
                    invoice.setInvoicePricing(listPricing);

                    personalTransaction.setPaymentInvoice(invoice);
                } catch (Exception e) {
                }
            }

            return personalTransaction;
        }
        return null;
    }

}

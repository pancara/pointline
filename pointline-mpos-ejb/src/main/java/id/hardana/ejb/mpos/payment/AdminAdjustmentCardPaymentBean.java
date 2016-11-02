/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.payment;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.entity.invoice.Invoice;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionCardHistory;
import id.hardana.entity.transaction.TransactionCardHistoryPhysic;
import id.hardana.entity.transaction.TransactionCardHistoryServer;
import id.hardana.entity.transaction.TransactionMerchantHistory;
import id.hardana.entity.transaction.TransactionPayment;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class AdminAdjustmentCardPaymentBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String INVOICE_STATUS_KEY = "invoiceStatus";
    private final String INVOICE_NUMBER_KEY = "invoiceNumber";
    private final String REFERENCE_NUMBER_KEY = "referenceNumber";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject pay(String merchantCode, String outletId,
            String amount, String pan, String cardFinalBalance) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        double amountDouble;
        double transactionFeeDouble;
        double totalAmountDouble;
        double debetAmountDouble;
        double cardFinalBalanceDouble;
        try {
            outletIdLong = Long.parseLong(outletId);
            amountDouble = Double.parseDouble(amount);
            transactionFeeDouble = 0;
            totalAmountDouble = amountDouble;
            debetAmountDouble = amountDouble;
            cardFinalBalanceDouble = Double.parseDouble(cardFinalBalance);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        BigDecimal amountBD = BigDecimal.valueOf(amountDouble);
        BigDecimal transactionFeeBD = BigDecimal.valueOf(transactionFeeDouble);
        BigDecimal totalAmountBD = BigDecimal.valueOf(totalAmountDouble);
        BigDecimal debetAmountBD = BigDecimal.valueOf(debetAmountDouble);
        BigDecimal cardFinalBalanceBD = BigDecimal.valueOf(cardFinalBalanceDouble);

        Date nowDate = new Date();

        List<Card> cardSearch = em.createQuery("SELECT c FROM Card c WHERE c.pan = :pan", Card.class)
                .setParameter("pan", pan)
                .getResultList();
        if (cardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            return response;
        }
        Card card = cardSearch.get(0);
        em.refresh(card);
        Long cardIdLong = Long.parseLong(card.getId());
        CardStatusEnum cardStatus = card.getStatusId();
        if (cardStatus.equals(CardStatusEnum.DISCARDED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_DISCARDED.getResponseStatus());
            return response;
        } else if (cardStatus.equals(CardStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_INACTIVE.getResponseStatus());
            return response;
        }
        Date cardExpirydate = null;
        try {
            cardExpirydate = DATE_FORMAT.parse(card.getExpiryDate());
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
            return response;
        }
        if (nowDate.after(cardExpirydate)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_EXPIRED.getResponseStatus());
            return response;
        }
        double balanceDouble = Double.parseDouble(card.getBalance());
        BigDecimal balance = BigDecimal.valueOf(balanceDouble);
        BigDecimal balanceAfterDebet = balance.subtract(totalAmountBD);
        if (balanceAfterDebet.compareTo(BigDecimal.ZERO) < 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_INSUFICIENT_BALANCE.getResponseStatus());
            return response;
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
        double merchantFeeDouble = Double.parseDouble(merchant.getMerchantFee());
        BigDecimal merchantFeeBD = BigDecimal.valueOf(merchantFeeDouble);

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

        List<Operator> operatorsSearch = em.createQuery("SELECT o FROM Operator o WHERE "
                + "o.isOwner = :isOwner AND o.merchantId = :merchantId", Operator.class)
                .setParameter("isOwner", true)
                .setParameter("merchantId", merchantId)
                .getResultList();
        Operator operatorSearch = operatorsSearch.get(0);
        Long operatorId = Long.parseLong(operatorSearch.getId());

        try {
            card.setBalance(balanceAfterDebet);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        boolean invoiceIsEmpty = false;
        String invoiceNumberGenerate = null;
        List<Invoice> listInvoice;

        while (!invoiceIsEmpty) {
            invoiceNumberGenerate = CodeGenerator.generateReferenceNumber();
            listInvoice = em.createQuery("SELECT i FROM Invoice i WHERE i.number = :number", Invoice.class)
                    .setParameter("number", invoiceNumberGenerate)
                    .getResultList();
            invoiceIsEmpty = listInvoice.isEmpty();
        }

        boolean transactionIsEmpty = false;
        String referenceNumberGenerate = null;
        List<TransactionTbl> transactionTbl;

        while (!transactionIsEmpty) {
            referenceNumberGenerate = CodeGenerator.generateReferenceNumber();
            transactionTbl = em.createQuery("SELECT t FROM TransactionTbl t WHERE t.referenceNumber = :referenceNumber", TransactionTbl.class)
                    .setParameter("referenceNumber", referenceNumberGenerate)
                    .getResultList();
            transactionIsEmpty = transactionTbl.isEmpty();
        }

        Invoice newInvoice = new Invoice();
        newInvoice.setAmount(amountBD);
        newInvoice.setDateTime(nowDate);
        newInvoice.setFee(merchantFeeBD);
        newInvoice.setMerchantId(merchantId);
        newInvoice.setNumber(invoiceNumberGenerate);
        newInvoice.setOperatorId(operatorId);
        newInvoice.setOutletId(outletIdLong);
        newInvoice.setStatus(InvoiceStatusEnum.PAID);
        newInvoice.setTableNumber("0");
        newInvoice.setClientDateTime(nowDate);
        em.persist(newInvoice);
        em.flush();

        Long invoiceId = Long.parseLong(newInvoice.getId());

        TransactionTbl newTransaction = new TransactionTbl();
        newTransaction.setAmount(amountBD);
        newTransaction.setClientTransRefnum("ADJ-ADM");
        newTransaction.setDateTime(nowDate);
        newTransaction.setFee(transactionFeeBD);
        newTransaction.setReferenceNumber(referenceNumberGenerate);
        newTransaction.setStatus(ResponseStatusEnum.SUCCESS);
        newTransaction.setTotalAmount(totalAmountBD);
        newTransaction.setType(TransactionTypeEnum.PAYMENT);
        newTransaction.setClientDateTime(nowDate);
        em.persist(newTransaction);
        em.flush();

        Long transactionId = Long.parseLong(newTransaction.getId());

        TransactionPayment newTransactionPayment = new TransactionPayment();
        newTransactionPayment.setInvoiceId(invoiceId);
        newTransactionPayment.setSourceId(cardIdLong);
        newTransactionPayment.setTransactionId(transactionId);
        newTransactionPayment.setType(TransactionPaymentTypeEnum.ADJUSTMENT_CASHCARD);
        em.persist(newTransactionPayment);
        em.flush();

        TransactionCardHistory cardHistory = new TransactionCardHistory();
        cardHistory.setCardId(cardIdLong);
        cardHistory.setTransactionId(transactionId);
        cardHistory.setCurrentBalanceP(cardFinalBalanceBD);
        cardHistory.setMovementP(debetAmountBD.negate());
        cardHistory.setCurrentBalanceS(balanceAfterDebet);
        cardHistory.setMovementS(totalAmountBD.negate());
        cardHistory.setCardStatus(cardStatus);
        em.persist(cardHistory);

        TransactionCardHistoryPhysic cardHistoryPhysic = new TransactionCardHistoryPhysic();
        cardHistoryPhysic.setCardId(cardIdLong);
        cardHistoryPhysic.setClientDateTime(nowDate);
        cardHistoryPhysic.setCurrentBalanceP(cardFinalBalanceBD);
        cardHistoryPhysic.setMovementP(debetAmountBD.negate());
        cardHistoryPhysic.setTransactionId(transactionId);
        em.persist(cardHistoryPhysic);

        TransactionCardHistoryServer cardHistoryServer = new TransactionCardHistoryServer();
        cardHistoryServer.setCardId(cardIdLong);
        cardHistoryServer.setCurrentBalanceS(balanceAfterDebet);
        cardHistoryServer.setDateTime(nowDate);
        cardHistoryServer.setMovementS(totalAmountBD.negate());
        cardHistoryServer.setTransactionId(transactionId);
        em.persist(cardHistoryServer);

        TransactionMerchantHistory merchantHistory = new TransactionMerchantHistory();
        merchantHistory.setMerchantId(merchantId);
        merchantHistory.setRefType(TransactionMerchantHistory.TRANSACTION);
        merchantHistory.setRefId(transactionId);
        em.persist(merchantHistory);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(INVOICE_STATUS_KEY, newInvoice.getStatus());
        response.put(INVOICE_NUMBER_KEY, newInvoice.getNumber());
        response.put(REFERENCE_NUMBER_KEY, referenceNumberGenerate);

        return response;
    }

}

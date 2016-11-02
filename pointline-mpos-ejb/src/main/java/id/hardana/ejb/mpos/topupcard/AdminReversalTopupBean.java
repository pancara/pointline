/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.topupcard;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionCardHistory;
import id.hardana.entity.transaction.TransactionCardHistoryPhysic;
import id.hardana.entity.transaction.TransactionCardHistoryServer;
import id.hardana.entity.transaction.TransactionMerchantHistory;
import id.hardana.entity.transaction.TransactionMerchantTopup;
import id.hardana.entity.transaction.TransactionMerchantTopupReversal;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
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

public class AdminReversalTopupBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject reversalTopup(String referenceNumber, String cardFinalBalance) {
        JSONObject response = new JSONObject();

        double cardFinalBalanceDouble;
        try {
            cardFinalBalanceDouble = Double.parseDouble(cardFinalBalance);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        BigDecimal cardFinalBalanceBD = BigDecimal.valueOf(cardFinalBalanceDouble);

        List<TransactionTbl> transactionSearch = em.createQuery("SELECT t FROM TransactionTbl t WHERE "
                + "t.referenceNumber = :referenceNumber AND t.type = :type", TransactionTbl.class)
                .setParameter("referenceNumber", referenceNumber)
                .setParameter("type", TransactionTypeEnum.MERCHANTTOPUP)
                .getResultList();
        if (transactionSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTbl transactionTopup = transactionSearch.get(0);

        ResponseStatusEnum statusTopup = transactionTopup.getStatus();
        if (!statusTopup.equals(ResponseStatusEnum.SUCCESS)) {
            if (statusTopup.equals(ResponseStatusEnum.PENDING)) {
                response.put(STATUS_KEY, ResponseStatusEnum.NO_NEED_REVERSAL_TOPUP.getResponseStatus());
                return response;
            } else {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_TOPUP_STATUS.getResponseStatus());
                return response;
            }
        }
        Long transactionId = Long.parseLong(transactionTopup.getId());
        double amount = Double.parseDouble(transactionTopup.getAmount());
        BigDecimal amountBD = BigDecimal.valueOf(amount);
        String clientTransRefnum = "REV-" + transactionTopup.getClientTransRefnum();
        BigDecimal topupFee = new BigDecimal(transactionTopup.getFee());

        List<TransactionMerchantTopupReversal> transactionreversalSearch = em.createQuery("SELECT t FROM TransactionMerchantTopupReversal t "
                + "WHERE t.transactionIdReference = :transactionIdReference", TransactionMerchantTopupReversal.class)
                .setParameter("transactionIdReference", transactionId)
                .getResultList();
        if (transactionreversalSearch.size() > 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.REVERSAL_TOPUP_HAS_BEEN_DONE.getResponseStatus());
            return response;
        }

        List<TransactionMerchantTopup> transactionMerchantTopupSearch = em.createQuery("SELECT t FROM "
                + "TransactionMerchantTopup t WHERE t.transactionId = :transactionId AND t.type = :type",
                TransactionMerchantTopup.class)
                .setParameter("transactionId", transactionId)
                .setParameter("type", TransactionMerchantTopupTypeEnum.CASHCARD)
                .getResultList();
        if (transactionMerchantTopupSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionMerchantTopup transactionMerchantTopup = transactionMerchantTopupSearch.get(0);
        Long cardId = Long.parseLong(transactionMerchantTopup.getTopupDestination());
        Long merchantId = Long.parseLong(transactionMerchantTopup.getMerchantId());
        Long outletId = Long.parseLong(transactionMerchantTopup.getOutletId());
        Long operatorId = Long.parseLong(transactionMerchantTopup.getOperatorId());

        List<Card> cardSearch = em.createNamedQuery("Card.findById", Card.class)
                .setParameter("id", cardId)
                .getResultList();
        if (cardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            return response;
        }
        Card card = cardSearch.get(0);
        em.refresh(card);
        double balanceDouble = Double.parseDouble(card.getBalance());
        BigDecimal balance = BigDecimal.valueOf(balanceDouble);
        BigDecimal balanceAfterReversalCredit = balance.subtract(amountBD);

        try {
            card.setBalance(balanceAfterReversalCredit);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();

        boolean transactionIsEmpty = false;
        String referenceNumberGenerate = null;
        List<TransactionTbl> transactionTbl;

        while (!transactionIsEmpty) {
            referenceNumberGenerate = CodeGenerator.generateReferenceNumber();
            transactionTbl = em.createQuery("SELECT t FROM TransactionTbl t WHERE "
                    + "t.referenceNumber = :referenceNumber", TransactionTbl.class)
                    .setParameter("referenceNumber", referenceNumberGenerate)
                    .getResultList();
            transactionIsEmpty = transactionTbl.isEmpty();
        }

        TransactionTbl newTransaction = new TransactionTbl();
        newTransaction.setAmount(amountBD);
        newTransaction.setClientTransRefnum(clientTransRefnum);
        newTransaction.setDateTime(nowDate);
        newTransaction.setFee(topupFee);
        newTransaction.setReferenceNumber(referenceNumberGenerate);
        newTransaction.setStatus(ResponseStatusEnum.SUCCESS);
        newTransaction.setTotalAmount(amountBD.add(topupFee));
        newTransaction.setType(TransactionTypeEnum.MERCHANTTOPUP);
        newTransaction.setClientDateTime(nowDate);
        em.persist(newTransaction);
        em.flush();

        Long newTransactionId = Long.parseLong(newTransaction.getId());
        TransactionMerchantTopup newTransactionMerchantTopup = new TransactionMerchantTopup();
        newTransactionMerchantTopup.setMerchantId(merchantId);
        newTransactionMerchantTopup.setOperatorId(operatorId);
        newTransactionMerchantTopup.setOutletId(outletId);
        newTransactionMerchantTopup.setTopupDestination(cardId);
        newTransactionMerchantTopup.setTransactionId(newTransactionId);
        newTransactionMerchantTopup.setType(TransactionMerchantTopupTypeEnum.MANUAL_REVERSAL_CASHCARD);
        em.persist(newTransactionMerchantTopup);
        em.flush();

        Long newTransactionMerchantTopupId = Long.parseLong(newTransactionMerchantTopup.getId());
        TransactionMerchantTopupReversal newTransactionMerchantTopupReversal = new TransactionMerchantTopupReversal();
        newTransactionMerchantTopupReversal.setTransactionMerchantTopupId(newTransactionMerchantTopupId);
        newTransactionMerchantTopupReversal.setTransactionIdReference(transactionId);
        em.persist(newTransactionMerchantTopupReversal);

        TransactionCardHistory cardHistory = new TransactionCardHistory();
        cardHistory.setCardId(cardId);
        cardHistory.setTransactionId(newTransactionId);
        cardHistory.setCurrentBalanceP(cardFinalBalanceBD);
        cardHistory.setMovementP(amountBD.negate());
        cardHistory.setCurrentBalanceS(balanceAfterReversalCredit);
        cardHistory.setMovementS(amountBD.negate());
        cardHistory.setCardStatus(card.getStatusId());
        em.persist(cardHistory);

        TransactionCardHistoryPhysic cardHistoryPhysic = new TransactionCardHistoryPhysic();
        cardHistoryPhysic.setCardId(cardId);
        cardHistoryPhysic.setClientDateTime(new Date());
        cardHistoryPhysic.setCurrentBalanceP(cardFinalBalanceBD);
        cardHistoryPhysic.setMovementP(amountBD.negate());
        cardHistoryPhysic.setTransactionId(newTransactionId);
        em.persist(cardHistoryPhysic);

        TransactionCardHistoryServer cardHistoryServer = new TransactionCardHistoryServer();
        cardHistoryServer.setCardId(cardId);
        cardHistoryServer.setCurrentBalanceS(balanceAfterReversalCredit);
        cardHistoryServer.setDateTime(new Date());
        cardHistoryServer.setMovementS(amountBD.negate());
        cardHistoryServer.setTransactionId(newTransactionId);
        em.persist(cardHistoryServer);

        TransactionMerchantHistory merchantHistory = new TransactionMerchantHistory();
        merchantHistory.setMerchantId(merchantId);
        merchantHistory.setRefType(TransactionMerchantHistory.TRANSACTION);
        merchantHistory.setRefId(transactionId);
        em.persist(merchantHistory);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

}

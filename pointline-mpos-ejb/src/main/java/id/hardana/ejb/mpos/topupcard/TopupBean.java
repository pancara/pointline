/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.topupcard;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionCardHistory;
import id.hardana.entity.transaction.TransactionCardHistoryPhysic;
import id.hardana.entity.transaction.TransactionCardHistoryServer;
import id.hardana.entity.transaction.TransactionMerchantHistory;
import id.hardana.entity.transaction.TransactionMerchantTopup;
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

public class TopupBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject topup(String merchantCode, String userName, String outletId,
            String referenceNumber, String creditAmount, String cardFinalBalance) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        double creditAmountDouble;
        double cardFinalBalanceDouble;
        try {
            outletIdLong = Long.parseLong(outletId);
            creditAmountDouble = Double.parseDouble(creditAmount);
            cardFinalBalanceDouble = Double.parseDouble(cardFinalBalance);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        BigDecimal creditAmountBD = BigDecimal.valueOf(creditAmountDouble);
        BigDecimal cardFinalBalanceBD = BigDecimal.valueOf(cardFinalBalanceDouble);

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
        if (!statusTopup.equals(ResponseStatusEnum.PENDING)) {
            if (statusTopup.equals(ResponseStatusEnum.SUCCESS)) {
                response.put(STATUS_KEY, ResponseStatusEnum.TOPUP_HAS_BEEN_DONE.getResponseStatus());
                return response;
            } else {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_INQUIRY_TOPUP_STATUS.getResponseStatus());
                return response;
            }
        }
        Long transactionId = Long.parseLong(transactionTopup.getId());
        double amount = Double.parseDouble(transactionTopup.getAmount());
        BigDecimal amountBD = BigDecimal.valueOf(amount);

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
        Long merchantIdInDB = Long.parseLong(transactionMerchantTopup.getMerchantId());
        Long outletIdInDB = Long.parseLong(transactionMerchantTopup.getOutletId());
        Long operatorIdInDB = Long.parseLong(transactionMerchantTopup.getOperatorId());
        if (merchantId.compareTo(merchantIdInDB) != 0
                || outletIdLong.compareTo(outletIdInDB) != 0
                || operatorId.compareTo(operatorIdInDB) != 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }

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
        BigDecimal balanceAfterCredit = balance.add(amountBD);

        try {
            card.setBalance(balanceAfterCredit);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        transactionTopup.setStatus(ResponseStatusEnum.SUCCESS);
        em.merge(transactionTopup);

        TransactionCardHistory cardHistory = new TransactionCardHistory();
        cardHistory.setCardId(cardId);
        cardHistory.setTransactionId(transactionId);
        cardHistory.setCurrentBalanceP(cardFinalBalanceBD);
        cardHistory.setMovementP(creditAmountBD);
        cardHistory.setCurrentBalanceS(balanceAfterCredit);
        cardHistory.setMovementS(amountBD);
        cardHistory.setCardStatus(card.getStatusId());
        em.persist(cardHistory);

        TransactionCardHistoryPhysic cardHistoryPhysic = new TransactionCardHistoryPhysic();
        cardHistoryPhysic.setCardId(cardId);
        cardHistoryPhysic.setClientDateTime(new Date());
        cardHistoryPhysic.setCurrentBalanceP(cardFinalBalanceBD);
        cardHistoryPhysic.setMovementP(creditAmountBD);
        cardHistoryPhysic.setTransactionId(transactionId);
        em.persist(cardHistoryPhysic);

        TransactionCardHistoryServer cardHistoryServer = new TransactionCardHistoryServer();
        cardHistoryServer.setCardId(cardId);
        cardHistoryServer.setCurrentBalanceS(balanceAfterCredit);
        cardHistoryServer.setDateTime(new Date());
        cardHistoryServer.setMovementS(amountBD);
        cardHistoryServer.setTransactionId(transactionId);
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

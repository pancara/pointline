/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.transfercard;

import id.hardana.ejb.mpos.holder.TransactionTransferCardHolder;
import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionCardHistory;
import id.hardana.entity.transaction.TransactionCardHistoryPhysic;
import id.hardana.entity.transaction.TransactionCardHistoryServer;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.transaction.TransactionTransfer;
import id.hardana.entity.transaction.TransactionTransferPersonalToCard;
import id.hardana.entity.transaction.enums.TransactionTransferPersonalToCardTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
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

public class TransferCardBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String TRANSFER_KEY = "transfer";
    private final String CREDIT_KEY_KEY = "creditKey";
    private final String MERCHANT_ID_KEY = "merchantId";
    private final String OUTLET_ID_KEY = "outletId";
    private final String OPERATOR_ID_KEY = "operatorId";
    private final String CARD_KEY = "card";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject inquiryTransferCard(String merchantCode, String userName,
            String outletId, String cardId) {
        JSONObject response = new JSONObject();

        JSONObject responseMerchantValidation = validateMerchant(merchantCode, userName, outletId);
        if (!responseMerchantValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responseMerchantValidation;
        }

        JSONObject responseCardValidation = validateCard(cardId);
        if (!responseCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responseCardValidation;
        }
        Card card = (Card) responseCardValidation.get(CARD_KEY);
        String creditKey = card.getCreditKey();
        Long idCard = Long.parseLong(card.getId());

        List<TransactionTransferCardHolder> transactionTransferList = em.createQuery("SELECT "
                + "NEW id.hardana.ejb.mpos.holder.TransactionTransferCardHolder(t.referenceNumber, t.amount, "
                + "tt.fromId, t.id) "
                + "FROM TransactionTbl t JOIN TransactionTransfer tt ON t.id = tt.transactionId "
                + "JOIN TransactionTransferPersonalToCard ttpt ON tt.id = ttpt.transactionTransferId "
                + "WHERE t.type = :transactionType AND t.status = :status AND tt.type = :transactionTransferType "
                + "AND tt.toId = :toId AND ttpt.type = :transactionTransferPersonalToCardType ORDER BY t.dateTime",
                TransactionTransferCardHolder.class)
                .setParameter("transactionType", TransactionTypeEnum.TRANSFER)
                .setParameter("status", ResponseStatusEnum.QUEUEING)
                .setParameter("transactionTransferType", TransactionTransferTypeEnum.PERSONALTOCARD)
                .setParameter("toId", idCard)
                .setParameter("transactionTransferPersonalToCardType", TransactionTransferPersonalToCardTypeEnum.VIA_MERCHANT)
                .getResultList();
        if (transactionTransferList.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }

        BigDecimal cardLimit = new BigDecimal(card.getTypeId().getBalanceLimit(em));
        BigDecimal cardBalance = new BigDecimal(card.getBalance());
        BigDecimal newCardBalance = cardBalance;

        try {
            for (TransactionTransferCardHolder transactionTransfer : transactionTransferList) {
                BigDecimal amountTransfer = transactionTransfer.getAmount();
                Long transactionId = transactionTransfer.getTransactionId();
                Long personalInfoId = transactionTransfer.getPersonalInfoId();
                newCardBalance = newCardBalance.add(amountTransfer);
                if (newCardBalance.compareTo(cardLimit) > 0) {
                    cancelTransfer(personalInfoId, transactionId);
                    transactionTransferList.remove(transactionTransfer);
                }
                transactionTransfer.setPersonalInfoId(null);
                transactionTransfer.setTransactionId(null);
            }
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(TRANSFER_KEY, transactionTransferList);
        response.put(CREDIT_KEY_KEY, creditKey);

        return response;
    }

    public JSONObject transferCard(String merchantCode, String userName, String outletId,
            String cardId, String referenceNumber, String cardFinalBalance, String amount) {
        JSONObject response = new JSONObject();

        BigDecimal cardFinalBalancePhysic;
        BigDecimal amountBD;
        try {
            cardFinalBalancePhysic = new BigDecimal(cardFinalBalance);
            amountBD = new BigDecimal(amount);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        JSONObject responseMerchantValidation = validateMerchant(merchantCode, userName, outletId);
        if (!responseMerchantValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responseMerchantValidation;
        }
        Long merchantId = responseMerchantValidation.getLong(MERCHANT_ID_KEY);
        Long outletIdValidate = responseMerchantValidation.getLong(OUTLET_ID_KEY);
        Long operatorId = responseMerchantValidation.getLong(OPERATOR_ID_KEY);

        Date nowDate = new Date();

        JSONObject responseCardValidation = validateCard(cardId);
        if (!responseCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responseCardValidation;
        }
        Card card = (Card) responseCardValidation.get(CARD_KEY);
        Long idCard = Long.parseLong(card.getId());

        List<TransactionTbl> transactionList = em.createQuery("SELECT t FROM TransactionTbl t WHERE "
                + "t.referenceNumber = :referenceNumber AND t.type = :type AND t.status = :status ",
                TransactionTbl.class)
                .setParameter("referenceNumber", referenceNumber)
                .setParameter("type", TransactionTypeEnum.TRANSFER)
                .setParameter("status", ResponseStatusEnum.QUEUEING)
                .getResultList();
        if (transactionList.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTbl transactionTbl = transactionList.get(0);
        BigDecimal amountTransaction = new BigDecimal(transactionTbl.getAmount());
        Long transactionId = Long.parseLong(transactionTbl.getId());

        if (amountBD.compareTo(amountTransaction) != 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_AMOUNT_TO_PAY.getResponseStatus());
            return response;
        }

        List<TransactionTransfer> transactionTransferSearch = em.createQuery("SELECT t FROM TransactionTransfer t WHERE "
                + "t.transactionId = :transactionId AND t.type = :type AND t.toId = :toId",
                TransactionTransfer.class)
                .setParameter("transactionId", transactionId)
                .setParameter("type", TransactionTransferTypeEnum.PERSONALTOCARD)
                .setParameter("toId", idCard)
                .getResultList();
        if (transactionTransferSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTransfer transactionTransfer = transactionTransferSearch.get(0);
        Long transactionTransferId = Long.parseLong(transactionTransfer.getId());

        List<TransactionTransferPersonalToCard> transactionTransferPersonalToCardSearch = em.createQuery("SELECT t "
                + "FROM TransactionTransferPersonalToCard t WHERE t.transactionTransferId = :transactionTransferId "
                + "AND t.type = :type",
                TransactionTransferPersonalToCard.class)
                .setParameter("transactionTransferId", transactionTransferId)
                .setParameter("type", TransactionTransferPersonalToCardTypeEnum.VIA_MERCHANT)
                .getResultList();
        if (transactionTransferPersonalToCardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTransferPersonalToCard transactionTransferPersonalToCard = transactionTransferPersonalToCardSearch.get(0);

        BigDecimal cardBalance = new BigDecimal(card.getBalance());
        BigDecimal newCardBalance = cardBalance.add(amountTransaction);

        try {
            card.setBalance(newCardBalance);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        transactionTbl.setStatus(ResponseStatusEnum.SUCCESS);
        em.merge(transactionTbl);

        transactionTransferPersonalToCard.setCompletionDateTime(nowDate);
        transactionTransferPersonalToCard.setClientCompletionDateTime(nowDate);
        transactionTransferPersonalToCard.setMerchantId(merchantId);
        transactionTransferPersonalToCard.setOperatorId(operatorId);
        transactionTransferPersonalToCard.setOutletId(outletIdValidate);
        em.merge(transactionTransferPersonalToCard);

        TransactionCardHistory cardHistory = new TransactionCardHistory();
        cardHistory.setCardId(idCard);
        cardHistory.setTransactionId(transactionId);
        cardHistory.setCurrentBalanceP(cardFinalBalancePhysic);
        cardHistory.setMovementP(amountBD);
        cardHistory.setCurrentBalanceS(newCardBalance);
        cardHistory.setMovementS(amountTransaction);
        cardHistory.setCardStatus(card.getStatusId());
        em.persist(cardHistory);

        TransactionCardHistoryPhysic cardHistoryPhysic = new TransactionCardHistoryPhysic();
        cardHistoryPhysic.setCardId(idCard);
        cardHistoryPhysic.setClientDateTime(nowDate);
        cardHistoryPhysic.setCurrentBalanceP(cardFinalBalancePhysic);
        cardHistoryPhysic.setMovementP(amountBD);
        cardHistoryPhysic.setTransactionId(transactionId);
        em.persist(cardHistoryPhysic);

        TransactionCardHistoryServer cardHistoryServer = new TransactionCardHistoryServer();
        cardHistoryServer.setCardId(idCard);
        cardHistoryServer.setCurrentBalanceS(newCardBalance);
        cardHistoryServer.setDateTime(nowDate);
        cardHistoryServer.setMovementS(amountTransaction);
        cardHistoryServer.setTransactionId(transactionId);
        em.persist(cardHistoryServer);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());

        return response;
    }

    public JSONObject reversalTransferCard(String merchantCode, String userName, String outletId,
            String cardId, String referenceNumber, String cardFinalBalance, String amount) {
        JSONObject response = new JSONObject();

        BigDecimal cardFinalBalancePhysic;
        BigDecimal amountBD;
        try {
            cardFinalBalancePhysic = new BigDecimal(cardFinalBalance);
            amountBD = new BigDecimal(amount);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        JSONObject responseMerchantValidation = validateMerchant(merchantCode, userName, outletId);
        if (!responseMerchantValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responseMerchantValidation;
        }
        Long merchantId = responseMerchantValidation.getLong(MERCHANT_ID_KEY);
        Long outletIdValidate = responseMerchantValidation.getLong(OUTLET_ID_KEY);
        Long operatorId = responseMerchantValidation.getLong(OPERATOR_ID_KEY);

        Date nowDate = new Date();

        JSONObject responseCardValidation = validateCard(cardId);
        if (!responseCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responseCardValidation;
        }
        Card card = (Card) responseCardValidation.get(CARD_KEY);
        Long idCard = Long.parseLong(card.getId());

        List<TransactionTbl> transactionList = em.createQuery("SELECT t FROM TransactionTbl t WHERE "
                + "t.referenceNumber = :referenceNumber AND t.type = :type AND t.status = :status ",
                TransactionTbl.class)
                .setParameter("referenceNumber", referenceNumber)
                .setParameter("type", TransactionTypeEnum.TRANSFER)
                .setParameter("status", ResponseStatusEnum.SUCCESS)
                .getResultList();
        if (transactionList.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTbl transactionTbl = transactionList.get(0);
        BigDecimal amountTransaction = new BigDecimal(transactionTbl.getAmount());
        Long transactionId = Long.parseLong(transactionTbl.getId());

        if (amountBD.compareTo(amountTransaction) != 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_AMOUNT_TO_PAY.getResponseStatus());
            return response;
        }

        List<TransactionTransfer> transactionTransferSearch = em.createQuery("SELECT t FROM TransactionTransfer t WHERE "
                + "t.transactionId = :transactionId AND t.type = :type AND t.toId = :toId",
                TransactionTransfer.class)
                .setParameter("transactionId", transactionId)
                .setParameter("type", TransactionTransferTypeEnum.PERSONALTOCARD)
                .setParameter("toId", idCard)
                .getResultList();
        if (transactionTransferSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTransfer transactionTransfer = transactionTransferSearch.get(0);
        Long transactionTransferId = Long.parseLong(transactionTransfer.getId());

        List<TransactionTransferPersonalToCard> transactionTransferPersonalToCardSearch = em.createQuery("SELECT t "
                + "FROM TransactionTransferPersonalToCard t WHERE t.transactionTransferId = :transactionTransferId "
                + "AND t.type = :type",
                TransactionTransferPersonalToCard.class)
                .setParameter("transactionTransferId", transactionTransferId)
                .setParameter("type", TransactionTransferPersonalToCardTypeEnum.VIA_MERCHANT)
                .getResultList();
        if (transactionTransferPersonalToCardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTransferPersonalToCard transactionTransferPersonalToCard = transactionTransferPersonalToCardSearch.get(0);

        BigDecimal cardBalance = new BigDecimal(card.getBalance());
        BigDecimal newCardBalance = cardBalance.subtract(amountTransaction);

        try {
            card.setBalance(newCardBalance);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        transactionTbl.setStatus(ResponseStatusEnum.QUEUEING);
        em.merge(transactionTbl);

        transactionTransferPersonalToCard.setCompletionDateTime(nowDate);
        transactionTransferPersonalToCard.setClientCompletionDateTime(nowDate);
        transactionTransferPersonalToCard.setMerchantId(merchantId);
        transactionTransferPersonalToCard.setOperatorId(operatorId);
        transactionTransferPersonalToCard.setOutletId(outletIdValidate);
        em.merge(transactionTransferPersonalToCard);

        TransactionCardHistory cardHistory = new TransactionCardHistory();
        cardHistory.setCardId(idCard);
        cardHistory.setTransactionId(transactionId);
        cardHistory.setCurrentBalanceP(cardFinalBalancePhysic);
        cardHistory.setMovementP(amountBD.negate());
        cardHistory.setCurrentBalanceS(newCardBalance);
        cardHistory.setMovementS(amountTransaction);
        cardHistory.setCardStatus(card.getStatusId());
        em.persist(cardHistory);

        TransactionCardHistoryPhysic cardHistoryPhysic = new TransactionCardHistoryPhysic();
        cardHistoryPhysic.setCardId(idCard);
        cardHistoryPhysic.setClientDateTime(nowDate);
        cardHistoryPhysic.setCurrentBalanceP(cardFinalBalancePhysic);
        cardHistoryPhysic.setMovementP(amountBD.negate());
        cardHistoryPhysic.setTransactionId(transactionId);
        em.persist(cardHistoryPhysic);

        TransactionCardHistoryServer cardHistoryServer = new TransactionCardHistoryServer();
        cardHistoryServer.setCardId(idCard);
        cardHistoryServer.setCurrentBalanceS(newCardBalance);
        cardHistoryServer.setDateTime(nowDate);
        cardHistoryServer.setMovementS(amountTransaction.negate());
        cardHistoryServer.setTransactionId(transactionId);
        em.persist(cardHistoryServer);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());

        return response;
    }

    private void cancelTransfer(Long personalInfoId, Long transactionId)
            throws OptimisticLockException {

        TransactionTbl transactionTbl = em.find(TransactionTbl.class, transactionId);
        BigDecimal totalAmount = new BigDecimal(transactionTbl.getTotalAmount());

        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (personalSearch.isEmpty()) {
            return;
        }
        Personal personal = personalSearch.get(0);
        BigDecimal personalBalance = new BigDecimal(personal.getBalance());
        BigDecimal newPersonalBalance = personalBalance.add(totalAmount);

        personal.setBalance(newPersonalBalance);
        em.merge(personal);

        transactionTbl.setStatus(ResponseStatusEnum.CANCELLED);
        em.merge(transactionTbl);
    }

    private JSONObject validateCard(String cardId) {
        JSONObject response = new JSONObject();
        Date nowDate = new Date();

        List<Card> cardSearch = em.createQuery("SELECT c FROM Card c WHERE c.cardId = :cardId", Card.class)
                .setParameter("cardId", cardId)
                .getResultList();
        if (cardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            return response;
        }
        Card card = cardSearch.get(0);
        em.refresh(card);
        CardStatusEnum cardStatus = card.getStatusId();
        if (cardStatus.equals(CardStatusEnum.BLACKLISTED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_BLACKLISTED.getResponseStatus());
            return response;
        } else if (cardStatus.equals(CardStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_BLOCKED.getResponseStatus());
            return response;
        } else if (cardStatus.equals(CardStatusEnum.DISCARDED)) {
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

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(CARD_KEY, card);
        return response;

    }

    private JSONObject validateMerchant(String merchantCode, String userName, String outletId) {
        JSONObject response = new JSONObject();
        Long outletIdLong;
        try {
            outletIdLong = Long.parseLong(outletId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
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

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(MERCHANT_ID_KEY, merchantId);
        response.put(OUTLET_ID_KEY, outletIdLong);
        response.put(OPERATOR_ID_KEY, operatorId);
        return response;
    }

}

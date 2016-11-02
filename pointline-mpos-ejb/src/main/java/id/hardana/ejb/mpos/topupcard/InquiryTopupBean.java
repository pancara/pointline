/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.topupcard;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.CardTypeEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionMerchantTopup;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.transaction.enums.TransactionFeeTypeEnum;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
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

public class InquiryTopupBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String REFERENCE_NUMBER_KEY = "referenceNumber";
    private final String CREDIT_KEY_KEY = "creditKey";
    private final String FEE_KEY = "fee";
    ;
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject inquiryTopup(String merchantCode, String userName, String outletId,
            String amount, String cardId, String clientTransRefnum) {
        JSONObject response = new JSONObject();

        Date nowDate = new Date();
        Long outletIdLong;
        Double amountDouble;
        try {
            outletIdLong = Long.parseLong(outletId);
            amountDouble = Double.parseDouble(amount);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        BigDecimal amountBD = BigDecimal.valueOf(amountDouble);

        String topupFeeString = TransactionFeeTypeEnum.MERCHANTTOPUPCARD.getFee(em);
        double topupFeeDouble = Double.parseDouble(topupFeeString);
        BigDecimal topupFee = BigDecimal.valueOf(topupFeeDouble);

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

        List<Card> cardSearch = em.createQuery("SELECT c FROM Card c WHERE c.cardId = :cardId", Card.class)
                .setParameter("cardId", cardId)
                .getResultList();
        if (cardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            return response;
        }
        Card card = cardSearch.get(0);
        em.refresh(card);
        Long cardIdLong = Long.parseLong(card.getId());
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
        CardTypeEnum cardType = card.getTypeId();
        double cardLimitDouble = Double.parseDouble(cardType.getBalanceLimit(em));
        BigDecimal cardLimit = BigDecimal.valueOf(cardLimitDouble);
        double balanceDouble = Double.parseDouble(card.getBalance());
        BigDecimal balance = BigDecimal.valueOf(balanceDouble);
        if ((balance.add(amountBD)).compareTo(cardLimit) > 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_BALANCE_OVER_LIMIT.getResponseStatus());
            return response;
        }
        String creditKey = card.getCreditKey();

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
        
        TransactionTbl newTransaction = new TransactionTbl();
        newTransaction.setAmount(amountBD);
        newTransaction.setClientTransRefnum(clientTransRefnum);
        newTransaction.setDateTime(nowDate);
        newTransaction.setFee(topupFee);
        newTransaction.setReferenceNumber(referenceNumberGenerate);
        newTransaction.setStatus(ResponseStatusEnum.PENDING);
        newTransaction.setTotalAmount(amountBD.add(topupFee));
        newTransaction.setType(TransactionTypeEnum.MERCHANTTOPUP);
        newTransaction.setClientDateTime(nowDate);
        em.persist(newTransaction);
        em.flush();
        
        Long newTransactionId = Long.parseLong(newTransaction.getId());
        TransactionMerchantTopup newTransactionMerchantTopup = new TransactionMerchantTopup();
        newTransactionMerchantTopup.setMerchantId(merchantId);
        newTransactionMerchantTopup.setOperatorId(operatorId);
        newTransactionMerchantTopup.setOutletId(outletIdLong);
        newTransactionMerchantTopup.setTopupDestination(cardIdLong);
        newTransactionMerchantTopup.setTransactionId(newTransactionId);
        newTransactionMerchantTopup.setType(TransactionMerchantTopupTypeEnum.CASHCARD);
        em.persist(newTransactionMerchantTopup);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(REFERENCE_NUMBER_KEY, referenceNumberGenerate);
        response.put(CREDIT_KEY_KEY, creditKey);
        response.put(FEE_KEY, topupFeeString);

        return response;
    }

}

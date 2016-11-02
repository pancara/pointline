/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.transaction;

import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.system.tools.SHA;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.profile.personal.PersonalToCard;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionPersonalHistory;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.transaction.TransactionTransfer;
import id.hardana.entity.transaction.TransactionTransferPersonalToCard;
import id.hardana.entity.transaction.enums.TransactionFeeTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferPersonalToCardTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

public class TransferToCardViaMerchantBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String PERSONAL_INFO_ID_KEY = "personalInfoId";
    private final String PERSONAL_KEY = "personal";
    private final String CARD_ID_KEY = "cardId";
    private final String CARD_KEY = "card";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject cancelTransferToCard(String account, String pan, String personalHashedPin,
            String referenceNumber, String clientDateTime) {
        JSONObject response = new JSONObject();

        JSONObject responsePersonalAndCardValidation = personalAndCardValidation(account, pan,
                personalHashedPin, clientDateTime);
        if (!responsePersonalAndCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalAndCardValidation;
        }

        Long personalInfoId = responsePersonalAndCardValidation.getLong(PERSONAL_INFO_ID_KEY);
        Long cardId = responsePersonalAndCardValidation.getLong(CARD_ID_KEY);
        Personal personal = (Personal) responsePersonalAndCardValidation.get(PERSONAL_KEY);

        List<TransactionTbl> transactionTblSearch = em.createQuery("SELECT t FROM TransactionTbl t WHERE "
                + "t.referenceNumber = :referenceNumber AND t.status = :status AND t.type = :type",
                TransactionTbl.class)
                .setParameter("referenceNumber", referenceNumber)
                .setParameter("status", ResponseStatusEnum.QUEUEING)
                .setParameter("type", TransactionTypeEnum.TRANSFER)
                .getResultList();
        if (transactionTblSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTbl transactionTbl = transactionTblSearch.get(0);
        Long transactionId = Long.parseLong(transactionTbl.getId());

        List<TransactionTransfer> transactionTransferSearch = em.createQuery("SELECT t FROM TransactionTransfer t WHERE "
                + "t.transactionId = :transactionId AND t.type = :type AND t.fromId = :fromId AND t.toId = :toId",
                TransactionTransfer.class)
                .setParameter("transactionId", transactionId)
                .setParameter("type", TransactionTransferTypeEnum.PERSONALTOCARD)
                .setParameter("fromId", personalInfoId)
                .setParameter("toId", cardId)
                .getResultList();
        if (transactionTransferSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        TransactionTransfer transactionTransfer = transactionTransferSearch.get(0);
        Long transactionTransferId = Long.parseLong(transactionTransfer.getId());

        List<TransactionTransferPersonalToCard> transactionTransferPersonalToCardSearch = em.createQuery("SELECT t "
                + "FROM TransactionTransferPersonalToCard t WHERE t.transactiontransferId = :transactiontransferId "
                + "AND t.type = :type",
                TransactionTransferPersonalToCard.class)
                .setParameter("transactionTransferId", transactionTransferId)
                .setParameter("type", TransactionTransferPersonalToCardTypeEnum.VIA_MERCHANT)
                .getResultList();
        if (transactionTransferPersonalToCardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }

        BigDecimal personalBalance = new BigDecimal(personal.getBalance());
        BigDecimal transactionAmount = new BigDecimal(transactionTbl.getTotalAmount());
        BigDecimal newPersonalBalance = personalBalance.add(transactionAmount);

        try {
            personal.setBalance(newPersonalBalance);
            em.merge(personal);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        transactionTbl.setStatus(ResponseStatusEnum.CANCELLED);
        em.merge(transactionTbl);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    public JSONObject transferToCard(String account, String pan, String personalHashedPin, String amount,
            String clientDateTime, String clientTransRefnum) {
        JSONObject response = new JSONObject();

        BigDecimal transactionFee = null;
        BigDecimal amountBD = null;
        Date cDateTime;
        try {
            transactionFee = new BigDecimal(TransactionFeeTypeEnum.TRANSFERPERSONALTOCARD.getFee(em));
            amountBD = new BigDecimal(amount);
            cDateTime = DATE_FORMAT.parse(clientDateTime);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        BigDecimal totalAmount = amountBD.add(transactionFee);
        if (totalAmount.compareTo(BigDecimal.ONE) == -1) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        JSONObject responsePersonalAndCardValidation = personalAndCardValidation(account, pan,
                personalHashedPin, clientDateTime);
        if (!responsePersonalAndCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalAndCardValidation;
        }

        Long personalInfoId = responsePersonalAndCardValidation.getLong(PERSONAL_INFO_ID_KEY);
        Long cardId = responsePersonalAndCardValidation.getLong(CARD_ID_KEY);
        Personal personal = (Personal) responsePersonalAndCardValidation.get(PERSONAL_KEY);
        Card card = (Card) responsePersonalAndCardValidation.get(CARD_KEY);

        BigDecimal personalBalance = new BigDecimal(personal.getBalance());
        BigDecimal cardBalance = new BigDecimal(card.getBalance());
        BigDecimal newPersonalBalance = personalBalance.subtract(totalAmount);
        if (newPersonalBalance.signum() == -1) {
            response.put(STATUS_KEY, ResponseStatusEnum.PERSONAL_INSUFICIENT_BALANCE.getResponseStatus());
            return response;
        }
        BigDecimal newCardBalance = cardBalance.add(amountBD);
        BigDecimal cardLimit = new BigDecimal(card.getTypeId().getBalanceLimit(em));
        if (newCardBalance.compareTo(cardLimit) == 1) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_BALANCE_OVER_LIMIT.getResponseStatus());
            return response;
        }

        Date nowDate = new Date();

        try {
            personal.setBalance(newPersonalBalance);
            em.merge(personal);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
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

        TransactionTbl newTransaction = new TransactionTbl();
        newTransaction.setAmount(amountBD);
        newTransaction.setClientTransRefnum(clientTransRefnum);
        newTransaction.setDateTime(nowDate);
        newTransaction.setFee(transactionFee);
        newTransaction.setReferenceNumber(referenceNumberGenerate);
        newTransaction.setStatus(ResponseStatusEnum.QUEUEING);
        newTransaction.setTotalAmount(totalAmount);
        newTransaction.setType(TransactionTypeEnum.TRANSFER);
        newTransaction.setClientDateTime(cDateTime);
        em.persist(newTransaction);
        em.flush();

        Long transactionId = Long.parseLong(newTransaction.getId());

        TransactionTransfer newTransactionTransfer = new TransactionTransfer();
        newTransactionTransfer.setFromId(personalInfoId);
        newTransactionTransfer.setToId(cardId);
        newTransactionTransfer.setTransactionId(transactionId);
        newTransactionTransfer.setType(TransactionTransferTypeEnum.PERSONALTOCARD);
        em.persist(newTransactionTransfer);
        em.flush();

        Long transactionTransferId = Long.parseLong(newTransactionTransfer.getId());

        TransactionTransferPersonalToCard newTransactionTransferPersonalToCard = new TransactionTransferPersonalToCard();
        newTransactionTransferPersonalToCard.setType(TransactionTransferPersonalToCardTypeEnum.VIA_MERCHANT);
        newTransactionTransferPersonalToCard.setTransactionTransferId(transactionTransferId);
        em.persist(newTransactionTransferPersonalToCard);

        TransactionPersonalHistory transactionPersonalHistory = new TransactionPersonalHistory();
        transactionPersonalHistory.setPersonalId(personalInfoId);
        transactionPersonalHistory.setTransactionId(transactionId);
        em.persist(transactionPersonalHistory);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    private JSONObject personalAndCardValidation(String account, String pan, String personalHashedPin,
            String clientDateTime) {
        JSONObject response = new JSONObject();

        JSONObject responsePersonalValidation = personalValidation(account, personalHashedPin, clientDateTime);
        if (!responsePersonalValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalValidation;
        }

        Long personalInfoId = responsePersonalValidation.getLong(PERSONAL_INFO_ID_KEY);
        Personal personal = (Personal) responsePersonalValidation.get(PERSONAL_KEY);

        List<Card> cardsSearch = em.createQuery("SELECT c FROM Card c WHERE c.pan = :pan", Card.class)
                .setParameter("pan", pan)
                .getResultList();
        if (cardsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_UNKNOWN.getResponseStatus());
            return response;
        }
        Card cardSearch = cardsSearch.get(0);
        em.refresh(cardSearch);
        Long cardId = Long.parseLong(cardSearch.getId());

        CardStatusEnum cardStatus = cardSearch.getStatusId();
        if (cardStatus.equals(CardStatusEnum.BLACKLISTED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_BLACKLISTED.getResponseStatus());
            return response;
        } else if (cardStatus.equals(CardStatusEnum.DISCARDED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_DISCARDED.getResponseStatus());
            return response;
        } else if (cardStatus.equals(CardStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_INACTIVE.getResponseStatus());
            return response;
        } else if (cardStatus.equals(CardStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_BLOCKED.getResponseStatus());
            return response;
        }

        List<PersonalToCard> personalToCardsSearch = em.createQuery("SELECT p FROM PersonalToCard p WHERE p.cardId = :cardId AND p.personalInfoId = :personalInfoId", PersonalToCard.class)
                .setParameter("cardId", cardId)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (personalToCardsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PERSONAL_INFO_ID_KEY, personalInfoId);
        response.put(PERSONAL_KEY, personal);
        response.put(CARD_ID_KEY, cardId);
        response.put(CARD_KEY, cardSearch);
        return response;
    }

    private JSONObject personalValidation(String account, String personalHashedPin, String clientDateTime) {
        JSONObject response = new JSONObject();
        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            return response;
        }

        List<PersonalInfo> personalInfoSearch = em.createNamedQuery("PersonalInfo.findByAccount", PersonalInfo.class)
                .setParameter("account", formattedAccount)
                .getResultList();
        if (personalInfoSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_NOT_REGISTERED.getResponseStatus());
            return response;
        }
        PersonalInfo personalInfo = personalInfoSearch.get(0);
        Long personalInfoId = Long.parseLong(personalInfo.getId());

        List<Personal> personalSearch = em.createNamedQuery("Personal.findByPersonalInfoId", Personal.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();

        Personal personal = personalSearch.get(0);
        em.refresh(personal);

        PersonalStatusEnum status = personal.getStatusId();
        if (status.equals(PersonalStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_BLOCKED.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.INCOMPLETE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INCOMPLETE_REGISTRATION.getResponseStatus());
            return response;
        } else if (status.equals(PersonalStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.ACCOUNT_INACTIVE.getResponseStatus());
            return response;
        }

        String pinFromDB = personal.getPin();
        if (pinFromDB == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PIN.getResponseStatus());
            return response;
        }
        String hashedPinFromDB = null;
        try {
            hashedPinFromDB = SHA.SHA256Hash(pinFromDB + clientDateTime);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TransferToCardViaMerchantBean.class.getName()).log(Level.SEVERE, null, ex);
            response.put(STATUS_KEY, ResponseStatusEnum.ENCRYPTION_ERROR.getResponseStatus());
            return response;
        }
        if (!personalHashedPin.equals(hashedPinFromDB)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PIN.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PERSONAL_INFO_ID_KEY, personalInfoId);
        response.put(PERSONAL_KEY, personal);
        return response;
    }

}

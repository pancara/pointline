/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.card;

import com.google.gson.Gson;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.webpersonal.holder.InvoiceHolder;
import id.hardana.ejb.webpersonal.holder.InvoiceItemHolder;
import id.hardana.ejb.webpersonal.holder.InvoicePricingHolder;
import id.hardana.ejb.webpersonal.holder.TransactionCardHolder;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.PersonalStatusEnum;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.profile.personal.PersonalToCard;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionMerchantTopupTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class PersonalToCardBean {

    private final String STATUS_KEY = "status";
    private final String PERSONAL_INFO_ID_KEY = "personalInfoId";
    private final String CARD_ID_KEY = "cardId";
    private final String CARD_PIN_KEY = "cardPin";
    private final String CARD_KEY = "card";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject addCard(String account, String pan, String pin, String cardHolderName) {
        JSONObject response = new JSONObject();

        JSONObject responsePersonalAndCardValidation = personalAndCardValidation(account, pan);
        if (!responsePersonalAndCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalAndCardValidation;
        }

        Long personalInfoId = responsePersonalAndCardValidation.getLong(PERSONAL_INFO_ID_KEY);
        Long cardId = responsePersonalAndCardValidation.getLong(CARD_ID_KEY);
        String cardPin = responsePersonalAndCardValidation.getString(CARD_PIN_KEY);

        if (!cardPin.equals(pin)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PIN.getResponseStatus());
            return response;
        }

        Card card = (Card) responsePersonalAndCardValidation.get(CARD_KEY);

        CardStatusEnum cardStatus = card.getStatusId();
        if (cardStatus.equals(CardStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_BLOCKED.getResponseStatus());
            return response;
        }

        List<PersonalToCard> personalToCardsSearch = em.createQuery("SELECT p FROM PersonalToCard p WHERE p.cardId = :cardId AND p.personalInfoId = :personalInfoId", PersonalToCard.class)
                .setParameter("cardId", cardId)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (!personalToCardsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_ALREADY_ADDED.getResponseStatus());
            return response;
        }

        PersonalToCard personalToCard = new PersonalToCard();
        personalToCard.setCardId(cardId);
        personalToCard.setPersonalInfoId(personalInfoId);
        em.persist(personalToCard);

        String cardHolderNameFromDB = card.getCardHolderName();
        if (cardHolderNameFromDB == null) {
            try {
                card.setCardHolderName(cardHolderName);
                em.merge(card);
            } catch (OptimisticLockException e) {
                context.setRollbackOnly();
                response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
                return response;
            }
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    public JSONObject deleteCard(String account, String pan) {
        JSONObject response = new JSONObject();

        JSONObject responsePersonalAndCardValidation = personalAndCardValidation(account, pan);
        if (!responsePersonalAndCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalAndCardValidation;
        }

        Long personalInfoId = responsePersonalAndCardValidation.getLong(PERSONAL_INFO_ID_KEY);
        Long cardId = responsePersonalAndCardValidation.getLong(CARD_ID_KEY);

        Card card = (Card) responsePersonalAndCardValidation.get(CARD_KEY);

        CardStatusEnum cardStatus = card.getStatusId();
        if (cardStatus.equals(CardStatusEnum.BLOCKED)) {
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

        PersonalToCard personalToCard = personalToCardsSearch.get(0);
        em.remove(personalToCard);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    public JSONObject blockCard(String account, String pan) {
        JSONObject response = new JSONObject();

        JSONObject responsePersonalAndCardValidation = personalAndCardValidation(account, pan);
        if (!responsePersonalAndCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalAndCardValidation;
        }

        Long personalInfoId = responsePersonalAndCardValidation.getLong(PERSONAL_INFO_ID_KEY);
        Long cardId = responsePersonalAndCardValidation.getLong(CARD_ID_KEY);

        List<PersonalToCard> personalToCardsSearch = em.createQuery("SELECT p FROM PersonalToCard p WHERE p.cardId = :cardId AND p.personalInfoId = :personalInfoId", PersonalToCard.class)
                .setParameter("cardId", cardId)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (personalToCardsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            return response;
        }

        Card card = (Card) responsePersonalAndCardValidation.get(CARD_KEY);

        CardStatusEnum cardStatus = card.getStatusId();
        if (cardStatus.equals(CardStatusEnum.BLOCKED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_ALREADY_BLOCKED.getResponseStatus());
            return response;
        }

        try {
            card.setStatusId(CardStatusEnum.BLOCKED);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    public JSONObject unblockCard(String account, String pan, String pin) {
        JSONObject response = new JSONObject();

        JSONObject responsePersonalAndCardValidation = personalAndCardValidation(account, pan);
        if (!responsePersonalAndCardValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalAndCardValidation;
        }

        Long personalInfoId = responsePersonalAndCardValidation.getLong(PERSONAL_INFO_ID_KEY);
        Long cardId = responsePersonalAndCardValidation.getLong(CARD_ID_KEY);
        String cardPin = responsePersonalAndCardValidation.getString(CARD_PIN_KEY);

        if (!cardPin.equals(pin)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_PIN.getResponseStatus());
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

        Card card = (Card) responsePersonalAndCardValidation.get(CARD_KEY);

        CardStatusEnum cardStatus = card.getStatusId();
        if (cardStatus.equals(CardStatusEnum.ACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_ALREADY_ACTIVE.getResponseStatus());
            return response;
        }

        try {
            card.setStatusId(CardStatusEnum.ACTIVE);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    public JSONObject listCard(String account) {
        JSONObject response = new JSONObject();

        JSONObject responsePersonalValidation = personalValidation(account);
        if (!responsePersonalValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalValidation;
        }

        Long personalInfoId = responsePersonalValidation.getLong(PERSONAL_INFO_ID_KEY);

        List<PersonalToCard> personalToCardsSearch = em.createQuery("SELECT p FROM PersonalToCard p WHERE p.personalInfoId = :personalInfoId", PersonalToCard.class)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (personalToCardsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_CARD.getResponseStatus());
            return response;
        }
        List<Long> cardIds = new ArrayList<Long>();
        for (PersonalToCard personalToCard : personalToCardsSearch) {
            cardIds.add(Long.parseLong(personalToCard.getCardId()));
        }
        List<Card> cardsSearch = em.createQuery("SELECT c FROM Card c WHERE c.id IN :ids", Card.class)
                .setParameter("ids", cardIds)
                .getResultList();
        for (Card card : cardsSearch) {
            em.refresh(card);
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(CARD_KEY, cardsSearch);

        JSONArray cardsArray = response.getJSONArray(CARD_KEY);
        JSONArray newCardsArray = new JSONArray();
        for (int i = 0; i < cardsArray.length(); i++) {
            JSONObject jOb = cardsArray.getJSONObject(i);
            try {
                Long cardIdLastTrx = Long.parseLong(jOb.getString("id"));
                TransactionCardHolder lastTransaction = lastTransaction(cardIdLastTrx);
                if (lastTransaction != null) {
                    jOb.put("lastTransaction", new JSONObject(new Gson().toJson(lastTransaction)));
                }
            } catch (Exception e) {
            }
            jOb.remove("id");
            jOb.remove("cardId");
            jOb.remove("creditKey");
            jOb.remove("pin");
            jOb.remove("statusKey");
            newCardsArray.put(jOb);
        }

        response.remove(CARD_KEY);
        response.put(CARD_KEY, newCardsArray);

        return response;
    }

    private JSONObject personalAndCardValidation(String account, String pan) {
        JSONObject response = new JSONObject();

        JSONObject responsePersonalValidation = personalValidation(account);
        if (!responsePersonalValidation.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return responsePersonalValidation;
        }

        List<Card> cardsSearch = em.createQuery("SELECT c FROM Card c WHERE c.pan = :pan", Card.class)
                .setParameter("pan", pan)
                .getResultList();
        if (cardsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_UNKNOWN.getResponseStatus());
            return response;
        }
        Card cardSearch = cardsSearch.get(0);
        em.refresh(cardSearch);

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
        }

        String cardPin = cardSearch.getPin();
        Long cardId = Long.parseLong(cardSearch.getId());
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PERSONAL_INFO_ID_KEY, responsePersonalValidation.getLong(PERSONAL_INFO_ID_KEY));
        response.put(CARD_ID_KEY, cardId);
        response.put(CARD_PIN_KEY, cardPin);
        response.put(CARD_KEY, cardSearch);
        return response;
    }

    private JSONObject personalValidation(String account) {
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

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PERSONAL_INFO_ID_KEY, personalInfoId);
        return response;
    }

    private TransactionCardHolder lastTransaction(Long cardId) {
        String debetCase = "(t.type = :typePayment AND tp.sourceId = c.id AND tp.type = :paymentType) "
                + "OR (t.type = :typeTransfer AND tt.type = :typeCTP AND tt.fromId = c.id)";
        String creditCase = "(t.type = :typeTopup AND tm.topupDestination = c.id AND tm.type = :topupType) "
                + "OR (t.type = :typeTransfer AND tt.type = :typePTC AND tt.toId = c.id)";
        String orderBy = "ORDER BY t.id DESC ";
        String queryCardHistory = "SELECT NEW id.hardana.ejb.webpersonal.holder.TransactionCardHolder "
                + "(t.id, t.referenceNumber, t.clientTransRefnum, "
                + "t.type, t.amount, t.fee, t.totalAmount, t.status, t.dateTime, "
                + "CASE WHEN " + debetCase + " THEN 'D' WHEN " + creditCase + " THEN 'C' ELSE 'D' END, "
                + "tp.invoiceId, m.brandName, o.name, o.latitude, o.longitude, "
                + "tt.type, pi.account, pi.firstName, pi.lastName, "
                + "po.account, po.firstName, po.lastName, c.pan, c.cardHolderName, "
                + "tp.type, tm.type) "
                + "FROM TransactionTbl t JOIN Card c "
                + "LEFT JOIN TransactionPayment tp ON t.id = tp.transactionId "
                + "LEFT JOIN TransactionMerchantTopup tm ON t.id = tm.transactionId "
                + "LEFT JOIN Merchant m ON m.id = tm.merchantId "
                + "LEFT JOIN Outlet o ON o.id = tm.outletId "
                + "LEFT JOIN TransactionTransfer tt ON t.id = tt.transactionId "
                + "LEFT JOIN PersonalInfo pi ON tt.fromId = pi.id "
                + "LEFT JOIN PersonalInfo po ON tt.toId = po.id "
                + "WHERE c.id = :id AND (" + debetCase + " OR " + creditCase + ") AND t.status = :status "
                + orderBy;
        List<TransactionCardHolder> cardTransactionList = em.createQuery(queryCardHistory, TransactionCardHolder.class)
                .setParameter("typePayment", TransactionTypeEnum.PAYMENT)
                .setParameter("typeTopup", TransactionTypeEnum.MERCHANTTOPUP)
                .setParameter("typeTransfer", TransactionTypeEnum.TRANSFER)
                .setParameter("typePTC", TransactionTransferTypeEnum.PERSONALTOCARD)
                .setParameter("typeCTP", TransactionTransferTypeEnum.CARDTOPERSONAL)
                .setParameter("paymentType", TransactionPaymentTypeEnum.CASHCARD)
                .setParameter("topupType", TransactionMerchantTopupTypeEnum.CASHCARD)
                .setParameter("id", cardId)
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.personal.PersonalToCard;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CardValidatorBean {

    private final String STATUS_KEY = "status";
    private final String CARD_KEY = "card";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public HashMap<String, Object> validatePersonalToCard(String pan, Long personalInfoId) {
        HashMap response = new HashMap();

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
        HashMap validateCardStatusResponse = validateCardStatus(cardStatus);
        if (!validateCardStatusResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateCardStatusResponse;
        }

        List<PersonalToCard> personalToCardsSearch = em.createQuery("SELECT p FROM PersonalToCard p "
                + "WHERE p.cardId = :cardId AND p.personalInfoId = :personalInfoId", PersonalToCard.class)
                .setParameter("cardId", cardId)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (personalToCardsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(CARD_KEY, cardSearch);

        return response;
    }

    public HashMap<String, Object> validateCardById(Long cardId) {
        HashMap response = new HashMap();

        List<Card> cardsSearch = em.createNamedQuery("Card.findById", Card.class)
                .setParameter("id", cardId)
                .getResultList();
        if (cardsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_UNKNOWN.getResponseStatus());
            return response;
        }
        Card cardSearch = cardsSearch.get(0);
        em.refresh(cardSearch);

        CardStatusEnum cardStatus = cardSearch.getStatusId();
        HashMap validateCardStatusResponse = validateCardStatus(cardStatus);
        if (!validateCardStatusResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return validateCardStatusResponse;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(CARD_KEY, cardSearch);

        return response;
    }

    private HashMap<String, Object> validateCardStatus(CardStatusEnum cardStatus) {
        HashMap response = new HashMap();

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

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;

    }

}

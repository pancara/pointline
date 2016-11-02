/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.enums;

import id.hardana.entity.profile.card.CardType;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum CardTypeEnum {

    UNREGISTER(0, new BigDecimal(1000000)),
    REGISTER(1, new BigDecimal(5000000));

    private int cardTypeId;
    private String cardType;
    private BigDecimal balanceLimit;

    private static Map<Integer, CardTypeEnum> codeToCardTypeMapping;

    private CardTypeEnum(int cardTypeId, BigDecimal balanceLimit) {
        this.cardTypeId = cardTypeId;
        this.cardType = name();
        this.balanceLimit = balanceLimit;
    }

    public static CardTypeEnum getCardType(int i) {
        if (codeToCardTypeMapping == null) {
            initMapping();
        }
        return codeToCardTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToCardTypeMapping = new HashMap<Integer, CardTypeEnum>();
        for (CardTypeEnum m : values()) {
            codeToCardTypeMapping.put(m.cardTypeId, m);
        }
    }

    public String getCardTypeId() {
        return String.valueOf(cardTypeId);
    }

    public String getCardType() {
        return cardType;
    }

    public String getDefaultBalanceLimit() {
        return balanceLimit.toPlainString();
    }

    public String getBalanceLimit(EntityManager em) {
        CardType cardTypeDB = em.find(CardType.class, Integer.parseInt(getCardTypeId()));
        if (cardTypeDB == null) {
            return balanceLimit.toPlainString();
        }
        em.refresh(cardTypeDB);
        return cardTypeDB.getLimitBalance();
    }

}

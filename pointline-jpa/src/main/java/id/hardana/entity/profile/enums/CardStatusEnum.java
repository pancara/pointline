/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum CardStatusEnum {

    INACTIVE(0),
    ACTIVE(1),
    BLOCKED(2),
    BLACKLISTED(3),
    DISCARDED(4);

    private int cardStatusId;
    private String cardStatus;

    private static Map<Integer, CardStatusEnum> codeToCardStatusMapping;

    private CardStatusEnum(int cardStatusId) {
        this.cardStatusId = cardStatusId;
        this.cardStatus = name();
    }

    public static CardStatusEnum getCardStatus(int i) {
        if (codeToCardStatusMapping == null) {
            initMapping();
        }
        return codeToCardStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToCardStatusMapping = new HashMap<Integer, CardStatusEnum>();
        for (CardStatusEnum m : values()) {
            codeToCardStatusMapping.put(m.cardStatusId, m);
        }
    }

    public String getCardStatusId() {
        return String.valueOf(cardStatusId);
    }

    public String getCardStatus() {
        return cardStatus;
    }
}

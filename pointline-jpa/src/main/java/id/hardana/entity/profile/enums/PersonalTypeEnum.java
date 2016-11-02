/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.enums;

import id.hardana.entity.profile.personal.PersonalType;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum PersonalTypeEnum {

    UNREGISTER(0, new BigDecimal(1000000)),
    REGISTER(1, new BigDecimal(5000000));

    private int personalTypeId;
    private String personalType;
    private BigDecimal balanceLimit;

    private static Map<Integer, PersonalTypeEnum> codeToPersonalTypeMapping;

    private PersonalTypeEnum(int personalTypeId, BigDecimal balanceLimit) {
        this.personalTypeId = personalTypeId;
        this.personalType = name();
        this.balanceLimit = balanceLimit;
    }

    public static PersonalTypeEnum getPersonalType(int i) {
        if (codeToPersonalTypeMapping == null) {
            initMapping();
        }
        return codeToPersonalTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToPersonalTypeMapping = new HashMap<Integer, PersonalTypeEnum>();
        for (PersonalTypeEnum m : values()) {
            codeToPersonalTypeMapping.put(m.personalTypeId, m);
        }
    }

    public String getPersonalTypeId() {
        return String.valueOf(personalTypeId);
    }

    public String getPersonalType() {
        return personalType;
    }

    public String getDefaultBalanceLimit() {
        return balanceLimit.toPlainString();
    }

    public String getBalanceLimit(EntityManager em) {
        PersonalType personalTypeDB = em.find(PersonalType.class, Integer.parseInt(getPersonalTypeId()));
        if (personalTypeDB == null) {
            return balanceLimit.toPlainString();
        }
        em.refresh(personalTypeDB);
        return personalTypeDB.getLimitBalance();
    }

}

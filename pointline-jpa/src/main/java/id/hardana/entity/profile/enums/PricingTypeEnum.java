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
public enum PricingTypeEnum {

    RUPIAH(0),
    PERCENTAGE(1),
    ROUNDING(2);

    private int pricingTypeId;
    private String pricingType;

    private static Map<Integer, PricingTypeEnum> codeToPricingTypeMapping;

    private PricingTypeEnum(int pricingTypeId) {
        this.pricingTypeId = pricingTypeId;
        this.pricingType = name();
    }

    public static PricingTypeEnum getPricingType(int i) {
        if (codeToPricingTypeMapping == null) {
            initMapping();
        }
        return codeToPricingTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToPricingTypeMapping = new HashMap<Integer, PricingTypeEnum>();
        for (PricingTypeEnum m : values()) {
            codeToPricingTypeMapping.put(m.pricingTypeId, m);
        }
    }

    public String getPricingTypeId() {
        return String.valueOf(pricingTypeId);
    }

    public String getPricingType() {
        return pricingType;
    }
}

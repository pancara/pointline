package id.hardana.entity.profile.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pancara
 */
public enum DiscountValueTypeEnum {
    PERCENTAGE(0),
    RUPIAH(1);

    private final int discountValueTypeId;
    private final String discountValueTypeDesc;

    private static Map<Integer, DiscountValueTypeEnum> valueMap;
    
    private DiscountValueTypeEnum(int discountCalculationTypeId) {
        this.discountValueTypeId = discountCalculationTypeId;
        this.discountValueTypeDesc = name();
    }

    public int  getDiscountValueTypeId() {
        return discountValueTypeId;
    }

    public String getDiscountItemTypeDesc() {
        return discountValueTypeDesc;
    }
    
    public static DiscountValueTypeEnum getValue(int i) {
        if (valueMap == null) {
            initMapping();
        }
        return valueMap.get(i);
    }

    private static void initMapping() {
        valueMap = new HashMap<Integer, DiscountValueTypeEnum>();
        for (DiscountValueTypeEnum v : values()) {
            valueMap.put(v.discountValueTypeId, v);
        }
    }
}

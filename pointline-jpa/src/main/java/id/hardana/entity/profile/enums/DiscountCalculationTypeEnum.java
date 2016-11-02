/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pancara
 */
public enum DiscountCalculationTypeEnum {
    MINUS(0),
    PLUS(1);

    private final int discountCalculationTypeId;
    private final String discountCalculationTypeDesc;

    private static Map<Integer, DiscountCalculationTypeEnum> valueMap;
    
    private DiscountCalculationTypeEnum(int discountCalculationTypeId) {
        this.discountCalculationTypeId = discountCalculationTypeId;
        this.discountCalculationTypeDesc = name();
    }

    public int  getDiscountCalculationTypeId() {
        return discountCalculationTypeId;
    }

    public String getDiscountItemTypeDesc() {
        return discountCalculationTypeDesc;
    }
    
    public static DiscountCalculationTypeEnum getValue(int i) {
        if (valueMap == null) {
            initMapping();
        }
        return valueMap.get(i);
    }

    private static void initMapping() {
        valueMap = new HashMap<>();
        for (DiscountCalculationTypeEnum v : values()) {
            valueMap.put(v.discountCalculationTypeId, v);
        }
    }
}

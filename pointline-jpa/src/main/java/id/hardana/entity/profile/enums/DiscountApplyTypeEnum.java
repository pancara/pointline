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
public enum DiscountApplyTypeEnum {
    ITEM(0),
    TRANSACTION(1);

    private final int discountApplyTypeId;
    private final String discountApplyTypeDesc;

    private static Map<Integer, DiscountApplyTypeEnum> valueMap;
     
    private DiscountApplyTypeEnum(int discountApplyTypeId) {
        this.discountApplyTypeId = discountApplyTypeId;
        this.discountApplyTypeDesc = name();
    }

    public int  getDiscountApplyTypeId() {
        return discountApplyTypeId;
    }

    public String getDiscountApplyTypeDesc() {
        return discountApplyTypeDesc;
    }
    
    public static DiscountApplyTypeEnum getValue(int i) {
        if (valueMap == null) {
            initMapping();
        }
        return valueMap.get(i);
    }

    private static void initMapping() {
        valueMap = new HashMap<Integer, DiscountApplyTypeEnum>();
        for (DiscountApplyTypeEnum v : values()) {
            valueMap.put(v.discountApplyTypeId, v);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum BillLateFeeTypeEnum {

    NO_FEE(0),
    PERCENTAGE(1),
    FIXED(2);

    private int billLateFeeTypeId;
    private String billLateFeeType;

    private static Map<Integer, BillLateFeeTypeEnum> codeToBillLateFeeTypeMapping;

    private BillLateFeeTypeEnum(int billLateFeeTypeId) {
        this.billLateFeeTypeId = billLateFeeTypeId;
        this.billLateFeeType = name();
    }

    public static BillLateFeeTypeEnum getBillLateFeeType(int i) {
        if (codeToBillLateFeeTypeMapping == null) {
            initMapping();
        }
        return codeToBillLateFeeTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToBillLateFeeTypeMapping = new HashMap<Integer, BillLateFeeTypeEnum>();
        for (BillLateFeeTypeEnum m : values()) {
            codeToBillLateFeeTypeMapping.put(m.billLateFeeTypeId, m);
        }
    }

    public String getBillLateFeeTypeId() {
        return String.valueOf(billLateFeeTypeId);
    }

    public String getBillLateFeeType() {
        return billLateFeeType;
    }
}

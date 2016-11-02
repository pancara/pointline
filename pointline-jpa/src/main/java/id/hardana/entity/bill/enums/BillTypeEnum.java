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
public enum BillTypeEnum {

    PERSONAL_BILL(0),
    MERCHANT_BILL(1);

    private int billTypeId;
    private String billType;

    private static Map<Integer, BillTypeEnum> codeToBillTypeMapping;

    private BillTypeEnum(int billTypeId) {
        this.billTypeId = billTypeId;
        this.billType = name();
    }

    public static BillTypeEnum getBillType(int i) {
        if (codeToBillTypeMapping == null) {
            initMapping();
        }
        return codeToBillTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToBillTypeMapping = new HashMap<Integer, BillTypeEnum>();
        for (BillTypeEnum m : values()) {
            codeToBillTypeMapping.put(m.billTypeId, m);
        }
    }

    public String getBillTypeId() {
        return String.valueOf(billTypeId);
    }

    public String getBillType() {
        return billType;
    }
}

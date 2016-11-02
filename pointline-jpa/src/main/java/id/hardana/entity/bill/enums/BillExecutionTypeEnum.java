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
public enum BillExecutionTypeEnum {

    NOW(0),
    SCHEDULE(1);

    private int billExecutionTypeId;
    private String billExecutionType;

    private static Map<Integer, BillExecutionTypeEnum> codeToBillExecutionTypeMapping;

    private BillExecutionTypeEnum(int billExecutionTypeId) {
        this.billExecutionTypeId = billExecutionTypeId;
        this.billExecutionType = name();
    }

    public static BillExecutionTypeEnum getBillExecutionType(int i) {
        if (codeToBillExecutionTypeMapping == null) {
            initMapping();
        }
        return codeToBillExecutionTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToBillExecutionTypeMapping = new HashMap<Integer, BillExecutionTypeEnum>();
        for (BillExecutionTypeEnum m : values()) {
            codeToBillExecutionTypeMapping.put(m.billExecutionTypeId, m);
        }
    }

    public String getBillExecutionTypeId() {
        return String.valueOf(billExecutionTypeId);
    }

    public String getBillExecutionType() {
        return billExecutionType;
    }
}

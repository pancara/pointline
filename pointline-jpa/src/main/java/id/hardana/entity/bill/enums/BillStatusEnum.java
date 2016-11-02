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
public enum BillStatusEnum {

    WAITING_RESPONSE(0),
    PAID(1),
    APPROVED(2),
    REJECTED(3),
    CANCELED(4),
    UNKNOWN(5);

    private int billStatusId;
    private String billStatus;

    private static Map<Integer, BillStatusEnum> codeToBillStatusMapping;

    private BillStatusEnum(int billStatusId) {
        this.billStatusId = billStatusId;
        this.billStatus = name();
    }

    public static BillStatusEnum getBillStatus(int i) {
        if (codeToBillStatusMapping == null) {
            initMapping();
        }
        return codeToBillStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToBillStatusMapping = new HashMap<Integer, BillStatusEnum>();
        for (BillStatusEnum m : values()) {
            codeToBillStatusMapping.put(m.billStatusId, m);
        }
    }

    public String getBillStatusId() {
        return String.valueOf(billStatusId);
    }

    public String getBillStatus() {
        return billStatus;
    }
}

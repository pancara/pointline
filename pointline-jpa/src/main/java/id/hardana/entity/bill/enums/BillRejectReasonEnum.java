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
public enum BillRejectReasonEnum {

    NONE(0),
    SPAM(1),
    FRAUD(2),
    INAPPROPRIATE(3),
    WRONG_INVOICE(4),
    OTHER(5);

    private int billRejectReasonId;
    private String billRejectReason;

    private static Map<Integer, BillRejectReasonEnum> codeToBillRejectReasonMapping;

    private BillRejectReasonEnum(int billRejectReasonId) {
        this.billRejectReasonId = billRejectReasonId;
        this.billRejectReason = name();
    }

    public static BillRejectReasonEnum getBillRejectReason(int i) {
        if (codeToBillRejectReasonMapping == null) {
            initMapping();
        }
        return codeToBillRejectReasonMapping.get(i);
    }

    private static void initMapping() {
        codeToBillRejectReasonMapping = new HashMap<Integer, BillRejectReasonEnum>();
        for (BillRejectReasonEnum m : values()) {
            codeToBillRejectReasonMapping.put(m.billRejectReasonId, m);
        }
    }

    public String getBillRejectReasonId() {
        return String.valueOf(billRejectReasonId);
    }

    public String getBillRejectReason() {
        return billRejectReason;
    }
}

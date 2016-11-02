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
public enum BillScheduleFailedActionEnum {

    AUTO_CANCEL(0),
    RESCHEDULE(1);

    private int billScheduleFailedActionId;
    private String billScheduleFailedAction;

    private static Map<Integer, BillScheduleFailedActionEnum> codeToBillScheduleFailedActionMapping;

    private BillScheduleFailedActionEnum(int billScheduleFailedActionId) {
        this.billScheduleFailedActionId = billScheduleFailedActionId;
        this.billScheduleFailedAction = name();
    }

    public static BillScheduleFailedActionEnum getBillScheduleFailedAction(int i) {
        if (codeToBillScheduleFailedActionMapping == null) {
            initMapping();
        }
        return codeToBillScheduleFailedActionMapping.get(i);
    }

    private static void initMapping() {
        codeToBillScheduleFailedActionMapping = new HashMap<Integer, BillScheduleFailedActionEnum>();
        for (BillScheduleFailedActionEnum m : values()) {
            codeToBillScheduleFailedActionMapping.put(m.billScheduleFailedActionId, m);
        }
    }

    public String getBillScheduleFailedActionId() {
        return String.valueOf(billScheduleFailedActionId);
    }

    public String getBillScheduleFailedAction() {
        return billScheduleFailedAction;
    }
}

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
public enum BillScheduleStatusEnum {

    PENDING(0),
    OPEN(1),
    CLOSED(2),
    MANUALLY_CLOSED(3),
    AUTO_CLOSED(4);

    private int billScheduleStatusId;
    private String billScheduleStatus;

    private static Map<Integer, BillScheduleStatusEnum> codeToBillScheduleStatusMapping;

    private BillScheduleStatusEnum(int billScheduleStatusId) {
        this.billScheduleStatusId = billScheduleStatusId;
        this.billScheduleStatus = name();
    }

    public static BillScheduleStatusEnum getBillScheduleStatus(int i) {
        if (codeToBillScheduleStatusMapping == null) {
            initMapping();
        }
        return codeToBillScheduleStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToBillScheduleStatusMapping = new HashMap<Integer, BillScheduleStatusEnum>();
        for (BillScheduleStatusEnum m : values()) {
            codeToBillScheduleStatusMapping.put(m.billScheduleStatusId, m);
        }
    }

    public String getBillScheduleStatusId() {
        return String.valueOf(billScheduleStatusId);
    }

    public String getBillScheduleStatus() {
        return billScheduleStatus;
    }
}

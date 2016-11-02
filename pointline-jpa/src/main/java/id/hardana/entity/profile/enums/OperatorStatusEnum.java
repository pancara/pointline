/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum OperatorStatusEnum {

    INACTIVE(0),
    ACTIVE(1);

    private int operatorStatusId;
    private String operatorStatus;

    private static Map<Integer, OperatorStatusEnum> codeToOperatorStatusMapping;

    private OperatorStatusEnum(int operatorStatusId) {
        this.operatorStatusId = operatorStatusId;
        this.operatorStatus = name();
    }

    public static OperatorStatusEnum getOperatorStatus(int i) {
        if (codeToOperatorStatusMapping == null) {
            initMapping();
        }
        return codeToOperatorStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToOperatorStatusMapping = new HashMap<Integer, OperatorStatusEnum>();
        for (OperatorStatusEnum m : values()) {
            codeToOperatorStatusMapping.put(m.operatorStatusId, m);
        }
    }

    public String getOperatorStatusId() {
        return String.valueOf(operatorStatusId);
    }

    public String getOperatorStatus() {
        return operatorStatus;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Arya
 */
public enum GroupOperatorStatusEnum {

    INACTIVE(0),
    ACTIVE(1);

    private int groupOperatorStatusId;
    private String groupOperatorStatus;

    private static Map<Integer, GroupOperatorStatusEnum> codeToGroupOperatorStatusMapping;

    private GroupOperatorStatusEnum(int groupOperatorStatusId) {
        this.groupOperatorStatusId = groupOperatorStatusId;
        this.groupOperatorStatus = name();
    }

    public static GroupOperatorStatusEnum getGroupOperatorStatus(int i) {
        if (codeToGroupOperatorStatusMapping == null) {
            initMapping();
        }
        return codeToGroupOperatorStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToGroupOperatorStatusMapping = new HashMap<Integer, GroupOperatorStatusEnum>();
        for (GroupOperatorStatusEnum m : values()) {
            codeToGroupOperatorStatusMapping.put(m.groupOperatorStatusId, m);
        }
    }

    public String getGroupOperatorStatusId() {
        return String.valueOf(groupOperatorStatusId);
    }

    public String getGroupOperatorStatus() {
        return groupOperatorStatus;
    }
}

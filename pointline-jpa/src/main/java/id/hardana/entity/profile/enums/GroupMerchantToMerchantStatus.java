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
public enum GroupMerchantToMerchantStatus {

    INACTIVE(0),
    ACTIVE(1),
    PENDING(2),
    ACCEPTED(3),
    DENIED(4),
    DELETEDBYMERCHANT(5),
    DELETEDBYGROUP(6)
    ;

    private int groupMerchantToMerchantStatusId;
    private String groupMerchantToMerchantStatus;

    private static Map<Integer, GroupMerchantToMerchantStatus> codeToMerchantStatusMapping;

    private GroupMerchantToMerchantStatus(int groupMerchantToMerchantStatusId) {
        this.groupMerchantToMerchantStatusId = groupMerchantToMerchantStatusId;
        this.groupMerchantToMerchantStatus = name();
    }

    private static void initMapping() {
        codeToMerchantStatusMapping = new HashMap<Integer, GroupMerchantToMerchantStatus>();
        for (GroupMerchantToMerchantStatus gm : values()) {
            codeToMerchantStatusMapping.put(gm.groupMerchantToMerchantStatusId, gm);
        }
    }

    public int getGroupMerchantToMerchantStatusId() {
        return groupMerchantToMerchantStatusId;
    }

    public void setGroupMerchantToMerchantStatusId(int groupMerchantToMerchantStatusId) {
        this.groupMerchantToMerchantStatusId = groupMerchantToMerchantStatusId;
    }

    public String getGroupMerchantToMerchantStatus() {
        return groupMerchantToMerchantStatus;
    }

    public void setGroupMerchantToMerchantStatus(String groupMerchantToMerchantStatus) {
        this.groupMerchantToMerchantStatus = groupMerchantToMerchantStatus;
    }

    public static Map<Integer, GroupMerchantToMerchantStatus> getCodeGroupMerchantToMerchantStatusMapping() {
        return codeToMerchantStatusMapping;
    }

    public static void setCodeGroupMerchantToMerchantStatusMapping(Map<Integer, GroupMerchantToMerchantStatus> codeToMerchantStatusMapping) {
        GroupMerchantToMerchantStatus.codeToMerchantStatusMapping = codeToMerchantStatusMapping;
    }
}

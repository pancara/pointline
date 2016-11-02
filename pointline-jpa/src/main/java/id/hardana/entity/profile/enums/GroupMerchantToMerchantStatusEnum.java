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
public enum GroupMerchantToMerchantStatusEnum {

    INACTIVE(0),
    ACTIVE(1),
    PENDING(2),
    ACCEPTED(3),
    DENIED(4),
    DELETED_BY_MERCHANT(5),
    DELETED_BY_GROUP(6)
    ;

    private int groupMerchantToMerchantStatusId;
    private String groupMerchantToMerchantStatus;

    private static Map<Integer, GroupMerchantToMerchantStatusEnum> codeToMerchantStatusMapping;

    private GroupMerchantToMerchantStatusEnum(int groupMerchantToMerchantStatusId) {
        this.groupMerchantToMerchantStatusId = groupMerchantToMerchantStatusId;
        this.groupMerchantToMerchantStatus = name();
    }

    private static void initMapping() {
        codeToMerchantStatusMapping = new HashMap<Integer, GroupMerchantToMerchantStatusEnum>();
        for (GroupMerchantToMerchantStatusEnum gm : values()) {
            codeToMerchantStatusMapping.put(gm.groupMerchantToMerchantStatusId, gm);
        }
    }

    public int getGroupMerchantStatusId() {
        return groupMerchantToMerchantStatusId;
    }

    public void setGroupMerchantStatusId(int groupMerchantToMerchantStatusId) {
        this.groupMerchantToMerchantStatusId = groupMerchantToMerchantStatusId;
    }

    public String getGroupMerchantStatus() {
        return groupMerchantToMerchantStatus;
    }

    public void setGroupMerchantStatus(String groupMerchantToMerchantStatus) {
        this.groupMerchantToMerchantStatus = groupMerchantToMerchantStatus;
    }

    public static Map<Integer, GroupMerchantToMerchantStatusEnum> getCodeGroupMerchantToMerchantStatusMapping() {
        return codeToMerchantStatusMapping;
    }

    public static void setCodeToMerchantStatusMapping(Map<Integer, GroupMerchantToMerchantStatusEnum> codeToMerchantStatusMapping) {
        GroupMerchantToMerchantStatusEnum.codeToMerchantStatusMapping = codeToMerchantStatusMapping;
    }
}

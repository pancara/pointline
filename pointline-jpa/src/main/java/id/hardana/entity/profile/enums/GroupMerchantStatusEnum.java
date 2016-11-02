/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.profile.enums;

import java.util.Map;

/**
 *
 * @author Arya
 */
public enum GroupMerchantStatusEnum {

    INACTIVE(0),
    ACTIVE(1);

    private int groupMerchantStatusId;
    private String groupMerchantStatus;

    private static Map<Integer, GroupMerchantStatusEnum> codeToMerchantStatusMapping;

    private GroupMerchantStatusEnum(int groupMerchantStatusId) {
        this.groupMerchantStatusId = groupMerchantStatusId;
        this.groupMerchantStatus = name();
    }

    public int getGroupMerchantStatusId() {
        return groupMerchantStatusId;
    }

    public void setGroupMerchantStatusId(int groupMerchantStatusId) {
        this.groupMerchantStatusId = groupMerchantStatusId;
    }

    public String getGroupMerchantStatus() {
        return groupMerchantStatus;
    }

    public void setGroupMerchantStatus(String groupMerchantStatus) {
        this.groupMerchantStatus = groupMerchantStatus;
    }

    public static Map<Integer, GroupMerchantStatusEnum> getCodeToMerchantStatusMapping() {
        return codeToMerchantStatusMapping;
    }

    public static void setCodeToMerchantStatusMapping(Map<Integer, GroupMerchantStatusEnum> codeToMerchantStatusMapping) {
        GroupMerchantStatusEnum.codeToMerchantStatusMapping = codeToMerchantStatusMapping;
    }
}

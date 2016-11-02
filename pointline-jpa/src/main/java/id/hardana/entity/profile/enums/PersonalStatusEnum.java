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
public enum PersonalStatusEnum {

    INACTIVE(0),
    ACTIVE(1),
    BLOCKED(2),
    INCOMPLETE(3),
    PENDINGUPGRADE(4);

    private int personalStatusId;
    private String personalStatus;

    private static Map<Integer, PersonalStatusEnum> codeToPersonalStatusMapping;

    private PersonalStatusEnum(int personalStatusId) {
        this.personalStatusId = personalStatusId;
        this.personalStatus = name();
    }

    public static PersonalStatusEnum getPersonalStatus(int i) {
        if (codeToPersonalStatusMapping == null) {
            initMapping();
        }
        return codeToPersonalStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToPersonalStatusMapping = new HashMap<Integer, PersonalStatusEnum>();
        for (PersonalStatusEnum m : values()) {
            codeToPersonalStatusMapping.put(m.personalStatusId, m);
        }
    }

    public String getPersonalStatusId() {
        return String.valueOf(personalStatusId);
    }

    public String getPersonalStatus() {
        return personalStatus;
    }
}

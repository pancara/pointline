/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.autodebet.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum AutodebetStatusEnum {

    OPEN(0),
    CLOSED(1),
    MANUAL_CLOSED(2),
    AUTO_CLOSED(3);

    private int autoDebetStatusId;
    private String autoDebetStatus;

    private static Map<Integer, AutodebetStatusEnum> codeToAutoDebetMapping;

    private AutodebetStatusEnum(int autoDebetStatusId) {
        this.autoDebetStatusId = autoDebetStatusId;
        this.autoDebetStatus = name();
    }

    public static AutodebetStatusEnum getAutoDebetStatus(int i) {
        if (codeToAutoDebetMapping == null) {
            initMapping();
        }
        return codeToAutoDebetMapping.get(i);
    }

    private static void initMapping() {
        codeToAutoDebetMapping = new HashMap<Integer, AutodebetStatusEnum>();
        for (AutodebetStatusEnum m : values()) {
            codeToAutoDebetMapping.put(m.autoDebetStatusId, m);
        }
    }

    public String getAutoDebetStatusId() {
        return String.valueOf(autoDebetStatusId);
    }

    public String getAutoDebetStatus() {
        return autoDebetStatus;
    }
}

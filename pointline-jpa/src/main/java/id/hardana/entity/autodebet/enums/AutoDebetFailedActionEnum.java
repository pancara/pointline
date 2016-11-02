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
public enum AutoDebetFailedActionEnum {

    AUTO_CLOSE(0),
    RESCHEDULE(1),
    RESCHEDULE_CUMULATIVE(2);

    private int autoDebetFailedActionId;
    private String autoDebetFailedAction;

    private static Map<Integer, AutoDebetFailedActionEnum> codeToAutoDebetFailedActionMapping;

    private AutoDebetFailedActionEnum(int autoDebetFailedActionId) {
        this.autoDebetFailedActionId = autoDebetFailedActionId;
        this.autoDebetFailedAction = name();
    }

    public static AutoDebetFailedActionEnum getAutoDebetFailedAction(int i) {
        if (codeToAutoDebetFailedActionMapping == null) {
            initMapping();
        }
        return codeToAutoDebetFailedActionMapping.get(i);
    }

    private static void initMapping() {
        codeToAutoDebetFailedActionMapping = new HashMap<Integer, AutoDebetFailedActionEnum>();
        for (AutoDebetFailedActionEnum m : values()) {
            codeToAutoDebetFailedActionMapping.put(m.autoDebetFailedActionId, m);
        }
    }

    public String getAutoDebetFailedActionId() {
        return String.valueOf(autoDebetFailedActionId);
    }

    public String getAutoDebetFailedAction() {
        return autoDebetFailedAction;
    }
}

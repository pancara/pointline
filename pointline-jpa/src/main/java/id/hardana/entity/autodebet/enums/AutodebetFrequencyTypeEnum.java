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
public enum AutodebetFrequencyTypeEnum {

    HOUR(0),
    DAY(1),
    MONTH(2),
    YEAR(3);

    private int autoDebetFrequencyId;
    private String autoDebetFrequency;

    private static Map<Integer, AutodebetFrequencyTypeEnum> codeToAutoDebetFrequency;

    private AutodebetFrequencyTypeEnum(int autoDebetFrequencyId) {
        this.autoDebetFrequencyId = autoDebetFrequencyId;
        this.autoDebetFrequency = name();
    }

    public static AutodebetFrequencyTypeEnum getAutoDebetFrequency(int i) {
        if (codeToAutoDebetFrequency == null) {
            initMapping();
        }
        return codeToAutoDebetFrequency.get(i);
    }

    private static void initMapping() {
        codeToAutoDebetFrequency = new HashMap<Integer, AutodebetFrequencyTypeEnum>();
        for (AutodebetFrequencyTypeEnum m : values()) {
            codeToAutoDebetFrequency.put(m.autoDebetFrequencyId, m);
        }
    }

    public String getAutoDebetFrequencyId() {
        return String.valueOf(autoDebetFrequencyId);
    }

    public String getAutoDebetFrequency() {
        return autoDebetFrequency;
    }
}

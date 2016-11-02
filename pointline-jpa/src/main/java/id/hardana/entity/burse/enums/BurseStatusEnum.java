/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.burse.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum BurseStatusEnum {

    PAID(0),
    HOLD(1),
    CREATED(2);

    private int burseStatusId;
    private String burseStatus;

    private static Map<Integer, BurseStatusEnum> codeToBurseStatusMapping;

    private BurseStatusEnum(int burseStatusId) {
        this.burseStatusId = burseStatusId;
        this.burseStatus = name();
    }

    public static BurseStatusEnum getBurseStatus(int i) {
        if (codeToBurseStatusMapping == null) {
            initMapping();
        }
        return codeToBurseStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToBurseStatusMapping = new HashMap<Integer, BurseStatusEnum>();
        for (BurseStatusEnum m : values()) {
            codeToBurseStatusMapping.put(m.burseStatusId, m);
        }
    }

    public String getBurseStatusId() {
        return String.valueOf(burseStatusId);
    }

    public String getBurseStatus() {
        return burseStatus;
    }
}

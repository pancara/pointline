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
public enum OutletStatusEnum {

    INACTIVE(0),
    ACTIVE(1);

    private int outletStatusId;
    private String outletStatus;

    private static Map<Integer, OutletStatusEnum> codeToOutletStatusMapping;

    private OutletStatusEnum(int outletStatusId) {
        this.outletStatusId = outletStatusId;
        this.outletStatus = name();
    }

    public static OutletStatusEnum getOutletStatus(int i) {
        if (codeToOutletStatusMapping == null) {
            initMapping();
        }
        return codeToOutletStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToOutletStatusMapping = new HashMap<Integer, OutletStatusEnum>();
        for (OutletStatusEnum m : values()) {
            codeToOutletStatusMapping.put(m.outletStatusId, m);
        }
    }

    public String getOutletStatusId() {
        return String.valueOf(outletStatusId);
    }

    public String getOutletStatus() {
        return outletStatus;
    }
}

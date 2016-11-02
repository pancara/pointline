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
public enum IdentityTypeEnum {

    KTP(0),
    SIM(1),
    PASPOR(2);

    private int identityTypeId;
    private String identityType;

    private static Map<Integer, IdentityTypeEnum> codeToIndentityTypeMapping;

    private IdentityTypeEnum(int identityTypeId) {
        this.identityTypeId = identityTypeId;
        this.identityType = name();
    }

    public static IdentityTypeEnum getIdentityType(int i) {
        if (codeToIndentityTypeMapping == null) {
            initMapping();
        }
        return codeToIndentityTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToIndentityTypeMapping = new HashMap<Integer, IdentityTypeEnum>();
        for (IdentityTypeEnum m : values()) {
            codeToIndentityTypeMapping.put(m.identityTypeId, m);
        }
    }

    public String getIdentityTypeId() {
        return String.valueOf(identityTypeId);
    }

    public String getIdentityType() {
        return identityType;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum ApplicationTypeEnum {

    PERSONAL(0),
    MPOS(1),
    WEBMERCHANT(2),
    WEBADMIN(3),
    WEBPERSONAL(4),
    LOGINPERSONAL(5),
    LOGINMPOS(6),
    LOGINWEBMERCHANT(7),
    LOGINWEBPERSONAL(8),
    GROUP_MERCHANT(9);

    private int applicationTypeId;
    private String applicationType;

    private static Map<Integer, ApplicationTypeEnum> codeToApplicationTypeMapping;

    private ApplicationTypeEnum(int applicationTypeId) {
        this.applicationTypeId = applicationTypeId;
        this.applicationType = name();
    }

    public static ApplicationTypeEnum getApplicationType(int i) {
        if (codeToApplicationTypeMapping == null) {
            initMapping();
        }
        return codeToApplicationTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToApplicationTypeMapping = new HashMap<Integer, ApplicationTypeEnum>();
        for (ApplicationTypeEnum m : values()) {
            codeToApplicationTypeMapping.put(m.applicationTypeId, m);
        }
    }

    public String getApplicationTypeId() {
        return String.valueOf(applicationTypeId);
    }

    public String getApplicationTypeName() {
        return applicationType;
    }
}

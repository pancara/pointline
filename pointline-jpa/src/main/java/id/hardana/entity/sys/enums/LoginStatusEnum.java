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
public enum LoginStatusEnum {

    LOGIN(0),
    LOGOUT(1),
    EXPIRED(2);

    private int loginStatusCode;
    private String loginStatus;

    private static Map<Integer, LoginStatusEnum> codeToLoginStatusMapping;

    private LoginStatusEnum(int loginStatusCode) {
        this.loginStatusCode = loginStatusCode;
        this.loginStatus = name();
    }

    public static LoginStatusEnum getLoginStatus(int i) {
        if (codeToLoginStatusMapping == null) {
            initMapping();
        }
        return codeToLoginStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToLoginStatusMapping = new HashMap<Integer, LoginStatusEnum>();
        for (LoginStatusEnum m : values()) {
            codeToLoginStatusMapping.put(m.loginStatusCode, m);
        }
    }

    public String getLoginStatusCode() {
        return String.valueOf(loginStatusCode);
    }

    public String getLoginStatus() {
        return loginStatus;
    }
}

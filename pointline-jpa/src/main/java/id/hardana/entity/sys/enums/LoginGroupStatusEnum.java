/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.sys.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Arya
 */
public enum LoginGroupStatusEnum {

    LOGIN(0),
    LOGOUT(1),
    EXPIRED(2);

    private int loginStatusCode;
    private String loginStatus;

    private static Map<Integer, LoginGroupStatusEnum> codeToLoginStatusMapping;

    private LoginGroupStatusEnum(int loginStatusCode) {
        this.loginStatusCode = loginStatusCode;
        this.loginStatus = name();
    }

    public static LoginGroupStatusEnum getLoginStatus(int i) {
        if (codeToLoginStatusMapping == null) {
            initMapping();
        }
        return codeToLoginStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToLoginStatusMapping = new HashMap<Integer, LoginGroupStatusEnum>();
        for (LoginGroupStatusEnum m : values()) {
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

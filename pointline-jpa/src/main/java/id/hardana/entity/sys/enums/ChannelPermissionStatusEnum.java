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
public enum ChannelPermissionStatusEnum {

    PERMITTED(0),
    DENIED(1),
    BLOCKED(2),
    DELETED(3);

    private int channelPermissionStatusCode;
    private String channelPermissionStatus;

    private static Map<Integer, ChannelPermissionStatusEnum> codeToChannelPermissionStatusMapping;

    private ChannelPermissionStatusEnum(int channelPermissionStatusCode) {
        this.channelPermissionStatusCode = channelPermissionStatusCode;
        this.channelPermissionStatus = name();
    }

    public static ChannelPermissionStatusEnum getChannelPermissionStatus(int i) {
        if (codeToChannelPermissionStatusMapping == null) {
            initMapping();
        }
        return codeToChannelPermissionStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToChannelPermissionStatusMapping = new HashMap<Integer, ChannelPermissionStatusEnum>();
        for (ChannelPermissionStatusEnum m : values()) {
            codeToChannelPermissionStatusMapping.put(m.channelPermissionStatusCode, m);
        }
    }

    public String getChannelPermissionStatusCode() {
        return String.valueOf(channelPermissionStatusCode);
    }

    public String getChannelPermissionStatus() {
        return channelPermissionStatus;
    }
}

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
public enum ChannelStatusEnum {

    INACTIVE(0),
    ACTIVE(1);

    private int channelStatusCode;
    private String channelStatus;

    private static Map<Integer, ChannelStatusEnum> codeToChannelStatusMapping;

    private ChannelStatusEnum(int channelStatusCode) {
        this.channelStatusCode = channelStatusCode;
        this.channelStatus = name();
    }

    public static ChannelStatusEnum getChannelStatus(int i) {
        if (codeToChannelStatusMapping == null) {
            initMapping();
        }
        return codeToChannelStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToChannelStatusMapping = new HashMap<Integer, ChannelStatusEnum>();
        for (ChannelStatusEnum m : values()) {
            codeToChannelStatusMapping.put(m.channelStatusCode, m);
        }
    }

    public String getChannelStatusCode() {
        return String.valueOf(channelStatusCode);
    }

    public String getChannelStatus() {
        return channelStatus;
    }
}

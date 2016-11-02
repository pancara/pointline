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
public enum MerchantStatusEnum {

    INACTIVE(0),
    ACTIVE(1);

    private int merchantStatusId;
    private String merchantStatus;

    private static Map<Integer, MerchantStatusEnum> codeToMerchantStatusMapping;

    private MerchantStatusEnum(int merchantStatusId) {
        this.merchantStatusId = merchantStatusId;
        this.merchantStatus = name();
    }

    public static MerchantStatusEnum getMechantStatus(int i) {
        if (codeToMerchantStatusMapping == null) {
            initMapping();
        }
        return codeToMerchantStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToMerchantStatusMapping = new HashMap<Integer, MerchantStatusEnum>();
        for (MerchantStatusEnum m : values()) {
            codeToMerchantStatusMapping.put(m.merchantStatusId, m);
        }
    }

    public String getMerchantStatusId() {
        return String.valueOf(merchantStatusId);
    }

    public String getMerchantStatus() {
        return merchantStatus;
    }
}

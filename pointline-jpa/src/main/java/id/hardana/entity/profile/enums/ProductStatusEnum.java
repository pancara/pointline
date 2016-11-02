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
public enum ProductStatusEnum {

    INACTIVE(0),
    ACTIVE(1);

    private int productStatusId;
    private String productStatus;

    private static Map<Integer, ProductStatusEnum> codeToProductStatusMapping;

    private ProductStatusEnum(int productStatusId) {
        this.productStatusId = productStatusId;
        this.productStatus = name();
    }

    public static ProductStatusEnum getProductStatus(int i) {
        if (codeToProductStatusMapping == null) {
            initMapping();
        }
        return codeToProductStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToProductStatusMapping = new HashMap<Integer, ProductStatusEnum>();
        for (ProductStatusEnum m : values()) {
            codeToProductStatusMapping.put(m.productStatusId, m);
        }
    }

    public String getProductStatusId() {
        return String.valueOf(productStatusId);
    }

    public String getProductStatus() {
        return productStatus;
    }
}

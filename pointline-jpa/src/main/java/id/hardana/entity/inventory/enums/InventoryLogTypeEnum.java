/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.inventory.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum InventoryLogTypeEnum {

    MERCHANT_SETTING_CHANGE(0),
    OUTLET_SETTING_CHANGE(1),
    ITEM_SETTING_CHANGE(2),
    CHANGE_MINIMUM_STOCK(3),
    RESTOCK(4),
    INVOICE(5),
    VOID_INVOICE(5),
    REVERSAL(6);

    private int inventoryLogTypeId;
    private String inventoryLogType;

    private static Map<Integer, InventoryLogTypeEnum> codeToInventoryLogTypeMapping;

    private InventoryLogTypeEnum(int inventoryLogTypeId) {
        this.inventoryLogTypeId = inventoryLogTypeId;
        this.inventoryLogType = name();
    }

    public static InventoryLogTypeEnum getInventoryLogType(int i) {
        if (codeToInventoryLogTypeMapping == null) {
            initMapping();
        }
        return codeToInventoryLogTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToInventoryLogTypeMapping = new HashMap<Integer, InventoryLogTypeEnum>();
        for (InventoryLogTypeEnum m : values()) {
            codeToInventoryLogTypeMapping.put(m.inventoryLogTypeId, m);
        }
    }

    public String getTransactionTypeId() {
        return String.valueOf(inventoryLogTypeId);
    }

    public String getTransactionType() {
        return inventoryLogType;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.bill.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum BillResponseByEnum {

    ADMIN(0),
    PERSONAL(1);

    private int billResponseById;
    private String billResponseBy;

    private static Map<Integer, BillResponseByEnum> codeToBillResponseByMapping;

    private BillResponseByEnum(int billResponseById) {
        this.billResponseById = billResponseById;
        this.billResponseBy = name();
    }

    public static BillResponseByEnum getBillResponseBy(int i) {
        if (codeToBillResponseByMapping == null) {
            initMapping();
        }
        return codeToBillResponseByMapping.get(i);
    }

    private static void initMapping() {
        codeToBillResponseByMapping = new HashMap<Integer, BillResponseByEnum>();
        for (BillResponseByEnum m : values()) {
            codeToBillResponseByMapping.put(m.billResponseById, m);
        }
    }

    public String getBillResponseById() {
        return String.valueOf(billResponseById);
    }

    public String getBillResponseBy() {
        return billResponseBy;
    }
}

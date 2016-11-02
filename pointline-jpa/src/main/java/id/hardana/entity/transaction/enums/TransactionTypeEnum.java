/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum TransactionTypeEnum {

    PAYMENT(0),
    MERCHANTTOPUP(1),
    UTILITY(2),
    TRANSFER(3),
    CASHOUT(4),
    PERSONALTOPUP(5);

    private int transactionTypeId;
    private String transactionType;

    private static Map<Integer, TransactionTypeEnum> codeToTransactionTypeMapping;

    private TransactionTypeEnum(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
        this.transactionType = name();
    }

    public static TransactionTypeEnum getTransactionType(int i) {
        if (codeToTransactionTypeMapping == null) {
            initMapping();
        }
        return codeToTransactionTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToTransactionTypeMapping = new HashMap<Integer, TransactionTypeEnum>();
        for (TransactionTypeEnum m : values()) {
            codeToTransactionTypeMapping.put(m.transactionTypeId, m);
        }
    }

    public String getTransactionTypeId() {
        return String.valueOf(transactionTypeId);
    }

    public String getTransactionType() {
        return transactionType;
    }

}

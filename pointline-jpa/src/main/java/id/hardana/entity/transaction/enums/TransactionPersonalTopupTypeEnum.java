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
public enum TransactionPersonalTopupTypeEnum {

    TRANSFER(0),
    VIRTUALACCOUNT(1),
    VIRTUALACCOUNT_REVERSAL(2);

    private int transactionPersonalTopupTypeId;
    private String transactionPersonalTopupType;

    private static Map<Integer, TransactionPersonalTopupTypeEnum> codeToTransactionPersonalTopupTypeMapping;

    private TransactionPersonalTopupTypeEnum(int transactionMerchantTopupTypeId) {
        this.transactionPersonalTopupTypeId = transactionMerchantTopupTypeId;
        this.transactionPersonalTopupType = name();
    }

    public static TransactionPersonalTopupTypeEnum getTransactionPersonalTopupType(int i) {
        if (codeToTransactionPersonalTopupTypeMapping == null) {
            initMapping();
        }
        return codeToTransactionPersonalTopupTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToTransactionPersonalTopupTypeMapping = new HashMap<Integer, TransactionPersonalTopupTypeEnum>();
        for (TransactionPersonalTopupTypeEnum m : values()) {
            codeToTransactionPersonalTopupTypeMapping.put(m.transactionPersonalTopupTypeId, m);
        }
    }

    public String getTransactionPersonalTopupTypeId() {
        return String.valueOf(transactionPersonalTopupTypeId);
    }

    public String getTransactionPersonalTopupType() {
        return transactionPersonalTopupType;
    }

}

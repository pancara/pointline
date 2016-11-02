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
public enum TransactionMerchantTopupTypeEnum {

    CASHCARD(0),
    PERSONAL(1),
    REVERSAL_CASHCARD(2),
    MANUAL_REVERSAL_CASHCARD(3);

    private int transactionMerchantTopupTypeId;
    private String transactionMerchantTopupType;

    private static Map<Integer, TransactionMerchantTopupTypeEnum> codeToTransactionMerchantTopupTypeMapping;

    private TransactionMerchantTopupTypeEnum(int transactionMerchantTopupTypeId) {
        this.transactionMerchantTopupTypeId = transactionMerchantTopupTypeId;
        this.transactionMerchantTopupType = name();
    }

    public static TransactionMerchantTopupTypeEnum getTransactionMerchantTopupType(int i) {
        if (codeToTransactionMerchantTopupTypeMapping == null) {
            initMapping();
        }
        return codeToTransactionMerchantTopupTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToTransactionMerchantTopupTypeMapping = new HashMap<Integer, TransactionMerchantTopupTypeEnum>();
        for (TransactionMerchantTopupTypeEnum m : values()) {
            codeToTransactionMerchantTopupTypeMapping.put(m.transactionMerchantTopupTypeId, m);
        }
    }

    public String getTransactionMerchantTopupTypeId() {
        return String.valueOf(transactionMerchantTopupTypeId);
    }

    public String getTransactionMerchantTopupType() {
        return transactionMerchantTopupType;
    }

}

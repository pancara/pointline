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
public enum TransactionTransferPersonalToCardTypeEnum {

    VIA_MERCHANT(0),
    VIA_PERSONAL(1);

    private int transactionTransferTypeId;
    private String transactionTransferType;

    private static Map<Integer, TransactionTransferPersonalToCardTypeEnum> codeToTransactionTransferTypeMapping;

    private TransactionTransferPersonalToCardTypeEnum(int transactionTransferTypeId) {
        this.transactionTransferTypeId = transactionTransferTypeId;
        this.transactionTransferType = name();
    }

    public static TransactionTransferPersonalToCardTypeEnum getTransactionTransferType(int i) {
        if (codeToTransactionTransferTypeMapping == null) {
            initMapping();
        }
        return codeToTransactionTransferTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToTransactionTransferTypeMapping = new HashMap<Integer, TransactionTransferPersonalToCardTypeEnum>();
        for (TransactionTransferPersonalToCardTypeEnum m : values()) {
            codeToTransactionTransferTypeMapping.put(m.transactionTransferTypeId, m);
        }
    }

    public String getTransactionTransferTypeId() {
        return String.valueOf(transactionTransferTypeId);
    }

    public String getTransactionTransferType() {
        return transactionTransferType;
    }

}

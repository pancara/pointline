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
public enum TransactionTransferTypeEnum {

    PERSONALTOPERSONAL(0),
    REMITTANCE(1),
    PERSONALTOCARD(2),
    CARDTOPERSONAL(3);

    private int transactionTransferTypeId;
    private String transactionTransferType;

    private static Map<Integer, TransactionTransferTypeEnum> codeToTransactionTransferTypeMapping;

    private TransactionTransferTypeEnum(int transactionTransferTypeId) {
        this.transactionTransferTypeId = transactionTransferTypeId;
        this.transactionTransferType = name();
    }

    public static TransactionTransferTypeEnum getTransactionTransferType(int i) {
        if (codeToTransactionTransferTypeMapping == null) {
            initMapping();
        }
        return codeToTransactionTransferTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToTransactionTransferTypeMapping = new HashMap<Integer, TransactionTransferTypeEnum>();
        for (TransactionTransferTypeEnum m : values()) {
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

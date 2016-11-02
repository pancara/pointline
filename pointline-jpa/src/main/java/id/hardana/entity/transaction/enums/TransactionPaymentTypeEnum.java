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
public enum TransactionPaymentTypeEnum {

    CASHCARD(0),
    PERSONAL(1),
    CASH(2),
    VOUCHER(3),
    CREDITCARD(4),
    CREDITCARDEDC(5),
    ADJUSTMENT_CASHCARD(6),
    DEBITCARD(7),
    REVERSAL_CASH(8),
    REVERSAL_CREDITCARDEDC(9),
    REVERSAL_CASH_CARD(10),
    REVERSAL_DEBITCARD(11),
    ;

    private int transactionPaymentTypeId;
    private String transactionPaymentType;

    private static Map<Integer, TransactionPaymentTypeEnum> codeToTransactionPaymentTypeMapping;

    private TransactionPaymentTypeEnum(int transactionPaymentTypeId) {
        this.transactionPaymentTypeId = transactionPaymentTypeId;
        this.transactionPaymentType = name();
    }

    public static TransactionPaymentTypeEnum getTransactionPaymentType(int i) {
        if (codeToTransactionPaymentTypeMapping == null) {
            initMapping();
        }
        return codeToTransactionPaymentTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToTransactionPaymentTypeMapping = new HashMap<Integer, TransactionPaymentTypeEnum>();
        for (TransactionPaymentTypeEnum m : values()) {
            codeToTransactionPaymentTypeMapping.put(m.transactionPaymentTypeId, m);
        }
    }

    public String getTransactionPaymentTypeId() {
        return String.valueOf(transactionPaymentTypeId);
    }

    public String getTransactionPaymentType() {
        return transactionPaymentType;
    }

}

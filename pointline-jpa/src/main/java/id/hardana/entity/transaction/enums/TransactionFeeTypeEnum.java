/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.transaction.enums;

import id.hardana.entity.transaction.TransactionFeeType;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum TransactionFeeTypeEnum {

    PAYMENTCASHCARD(0, new BigDecimal(0)),
    PAYMENTPERSONAL(1, new BigDecimal(0)),
    PAYMENTCASH(2, new BigDecimal(0)),
    PAYMENTVOUCHER(3, new BigDecimal(0)),
    PAYMENTCC(4, new BigDecimal(0)),
    PAYMENTCCEDC(5, new BigDecimal(0)),
    MERCHANTTOPUPPERSONAL(6, new BigDecimal(0)),
    MERCHANTTOPUPCARD(7, new BigDecimal(0)),
    TRANSFERPERSONAL(8, new BigDecimal(0)),
    TRANSFERREMITTANCE(9, new BigDecimal(0)),
    TRANSFERPERSONALTOCARD(10, new BigDecimal(0)),
    TRANSFERCARDTOPERSONAL(11, new BigDecimal(0)),
    CASHOUT(12, new BigDecimal(0)),
    PERSONALTOPUP(13, new BigDecimal(0));

    private int transactionFeeTypeId;
    private String transactionFeeType;
    private BigDecimal defaultFee;
    private BigDecimal fee;

    private static Map<Integer, TransactionFeeTypeEnum> codeToTRansactionFeeTypeMapping;

    private TransactionFeeTypeEnum(int transactionFeeTypeId, BigDecimal defaultFee) {
        this.transactionFeeTypeId = transactionFeeTypeId;
        this.transactionFeeType = name();
        this.defaultFee = defaultFee;
    }

    public static TransactionFeeTypeEnum getTransactionFeeType(int i) {
        if (codeToTRansactionFeeTypeMapping == null) {
            initMapping();
        }
        return codeToTRansactionFeeTypeMapping.get(i);
    }

    private static void initMapping() {
        codeToTRansactionFeeTypeMapping = new HashMap<Integer, TransactionFeeTypeEnum>();
        for (TransactionFeeTypeEnum m : values()) {
            codeToTRansactionFeeTypeMapping.put(m.transactionFeeTypeId, m);
        }
    }

    public String getTransactionFeeTypeId() {
        return String.valueOf(transactionFeeTypeId);
    }

    public String getTransactionFeeType() {
        return transactionFeeType;
    }

    public String getFee(EntityManager em) {
        TransactionFeeType transactionFeeTypeFromDB = em.find(TransactionFeeType.class, Integer.parseInt(getTransactionFeeTypeId()));
        if (transactionFeeTypeFromDB == null) {
            return defaultFee.toPlainString();
        }
        em.refresh(transactionFeeTypeFromDB);
        fee = new BigDecimal(transactionFeeTypeFromDB.getFee());
        return fee.toPlainString();
    }

    public void setFee(EntityManager em, BigDecimal newFee) {
        TransactionFeeType transactionFeeTypeFromDB = em.find(TransactionFeeType.class, Integer.parseInt(getTransactionFeeTypeId()));
        if (transactionFeeTypeFromDB != null) {
            transactionFeeTypeFromDB.setFee(newFee);
            em.merge(transactionFeeTypeFromDB);
        }
    }

    public String getDefaultFee() {
        return defaultFee.toPlainString();
    }

}

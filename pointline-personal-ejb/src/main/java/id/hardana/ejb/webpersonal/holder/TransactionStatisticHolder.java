/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.holder;

import java.math.BigDecimal;

/**
 *
 * @author Trisna
 */
public class TransactionStatisticHolder {

    private Long totalTransaction;
    private Long debetTransaction;
    private Long creditTransaction;
    private TransactionCardHolder lastTransactionCard;
    private TransactionPersonalHolder lastTransactionPersonal;
    private String lastLogin;
    private String lastLogout;
    private BigDecimal totalAmount;
    private BigDecimal debetAmount;
    private BigDecimal creditAmount;

    public TransactionStatisticHolder(Long totalTransaction, Long debetTransaction, Long creditTransaction,
            BigDecimal totalAmount, BigDecimal debetAmount, BigDecimal creditAmount) {
        this.totalTransaction = totalTransaction;
        this.debetTransaction = debetTransaction;
        this.creditTransaction = creditTransaction;
        this.totalAmount = totalAmount;
        this.debetAmount = debetAmount;
        this.creditAmount = creditAmount;
    }

    public String getTotalTransaction() {
        if (totalTransaction == null) {
            return "0";
        }
        return String.valueOf(totalTransaction);
    }

    public void setTotalTransaction(Long totalTransaction) {
        this.totalTransaction = totalTransaction;
    }

    public String getDebetTransaction() {
        if (debetTransaction == null) {
            return "0";
        }
        return String.valueOf(debetTransaction);
    }

    public void setDebetTransaction(Long debetTransaction) {
        this.debetTransaction = debetTransaction;
    }

    public String getCreditTransaction() {
        if (creditTransaction == null) {
            return "0";
        }
        return String.valueOf(creditTransaction);
    }

    public void setCreditTransaction(Long creditTransaction) {
        this.creditTransaction = creditTransaction;
    }

    public TransactionCardHolder getLastTransactionCard() {
        return lastTransactionCard;
    }

    public void setLastTransactionCard(TransactionCardHolder lastTransactionCard) {
        this.lastTransactionCard = lastTransactionCard;
    }

    public TransactionPersonalHolder getLastTransactionPersonal() {
        return lastTransactionPersonal;
    }

    public void setLastTransactionPersonal(TransactionPersonalHolder lastTransactionPersonal) {
        this.lastTransactionPersonal = lastTransactionPersonal;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(String lastLogout) {
        this.lastLogout = lastLogout;
    }

    public String getTotalAmount() {
        if (totalAmount == null) {
            return "0";
        }
        return totalAmount.toPlainString();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDebetAmount() {
        if (debetAmount == null) {
            return "0";
        }
        return debetAmount.toPlainString();
    }

    public void setDebetAmount(BigDecimal debetAmount) {
        this.debetAmount = debetAmount;
    }

    public String getCreditAmount() {
        if (creditAmount == null) {
            return "0";
        }
        return creditAmount.toPlainString();
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.invoice;

import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.holder.transaction.TransactionPaymentHolder;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Trisna
 */
public class InvoiceHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Long id;
    private String number;
    private BigDecimal amount;
    private Date dateTime;
    private String tableNumber;
    private InvoiceStatusEnum status;
    private String operatorName;
    private String outletName;
    private double outletLatitude;
    private double outletLongitude;
    private List<InvoiceItemHolder> invoiceItems;
    private InvoiceTransactionDiscountHolder transactionDiscount;
    private List<InvoicePricingHolder> invoicePricing;
    private List<TransactionPaymentHolder> transactions;

    public InvoiceHolder(Long id, String number, BigDecimal amount, Date dateTime,
            String tableNumber, InvoiceStatusEnum status, String operatorName,
            String outletName, double outletLatitude, double outletLongitude) {
        this.id = id;
        this.number = number;
        this.amount = amount;
        this.dateTime = dateTime;
        this.tableNumber = tableNumber;
        this.status = status;
        this.operatorName = operatorName;
        this.outletName = outletName;
        this.outletLatitude = outletLatitude;
        this.outletLongitude = outletLongitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAmount() {
        return amount.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDateTime() {
        return DATE_FORMAT.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public InvoiceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatusEnum status) {
        this.status = status;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public double getOutletLatitude() {
        return outletLatitude;
    }

    public void setOutletLatitude(double outletLatitude) {
        this.outletLatitude = outletLatitude;
    }

    public double getOutletLongitude() {
        return outletLongitude;
    }

    public void setOutletLongitude(double outletLongitude) {
        this.outletLongitude = outletLongitude;
    }

    public List<InvoiceItemHolder> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItemHolder> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public InvoiceTransactionDiscountHolder getTransactionDiscount() {
        return transactionDiscount;
    }

    public void setTransactionDiscount(InvoiceTransactionDiscountHolder transactionDiscount) {
        this.transactionDiscount = transactionDiscount;
    }

    public List<InvoicePricingHolder> getInvoicePricing() {
        return invoicePricing;
    }

    public void setInvoicePricing(List<InvoicePricingHolder> invoicePricing) {
        this.invoicePricing = invoicePricing;
    }

    public List<TransactionPaymentHolder> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionPaymentHolder> transactions) {
        this.transactions = transactions;
    }

}

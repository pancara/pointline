/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class ItemsReportHolder {

    private Date dateTime;
    private Long itemId;
    private String itemName;
    private Long quantity;
    private BigDecimal amount;

    public ItemsReportHolder(Date dateTime, Long itemId, String itemName, Long quantity, BigDecimal amount) {
        this.dateTime = dateTime;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.amount = amount;
    }

    public ItemsReportHolder(Long itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}

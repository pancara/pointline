/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory.extension;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class InventoryItemHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Long outletId;
    private String outletName;
    private Long itemId;
    private String itemName;
    private Long stock;
    private Long minimumStock;
    private String updatedBy;
    private Date lastUpdated;
    private Boolean isActive;

    public InventoryItemHolder(Long outletId, String outletName, Long itemId, String itemName,
            Long stock, Long minimumStock, String updatedBy, Date lastUpdated, Boolean isActive) {
        this.outletId = outletId;
        this.outletName = outletName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.stock = stock;
        this.minimumStock = minimumStock;
        this.updatedBy = updatedBy;
        this.lastUpdated = lastUpdated;
        this.isActive = isActive;
    }

    public String getOutletId() {
        return outletId == null ? null : String.valueOf(outletId);
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getItemId() {
        return itemId == null ? null : String.valueOf(itemId);
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

    public String getStock() {
        return stock == null ? null : String.valueOf(stock);
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getMinimumStock() {
        return minimumStock == null ? null : String.valueOf(minimumStock);
    }

    public void setMinimumStock(Long minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getLastUpdated() {
        return lastUpdated == null ? null : DATE_FORMAT.format(lastUpdated);
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getIsActive() {
        return isActive == null ? "false" : Boolean.toString(isActive);
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}

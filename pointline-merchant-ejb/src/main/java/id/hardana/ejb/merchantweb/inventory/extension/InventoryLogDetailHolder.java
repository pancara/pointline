/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory.extension;

import java.text.SimpleDateFormat;

/**
 *
 * @author Trisna
 */
public class InventoryLogDetailHolder {

    private Long outletId;
    private String outletName;
    private Long itemId;
    private String itemName;
    private Long itemQuantity;
    private String description;

    public InventoryLogDetailHolder(Long outletId, String outletName, Long itemId, String itemName, 
            Long itemQuantity, String description) {
        this.outletId = outletId;
        this.outletName = outletName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.description = description;
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

    public String getItemQuantity() {
        return itemQuantity == null ? null : String.valueOf(itemQuantity);
    }

    public void setItemQuantity(Long itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

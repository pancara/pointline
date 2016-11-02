/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory.extension;

/**
 *
 * @author Trisna
 */
public class ItemRequestHolder {

    private Long outletId;
    private Long itemId;
    private String itemName;
//    for Restock and Set Minimum Stock
    private Long stock;
//    for Setting
    private Boolean isActive;

    public ItemRequestHolder(Long outletId, Long itemId, String itemName) {
        this.outletId = outletId;
        this.itemId = itemId;
        this.itemName = itemName;
    }

//    for Restock and Set Minimum Stock
    public ItemRequestHolder(Long outletId, Long itemId, String itemName, Long stock) {
        this.outletId = outletId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.stock = stock;
    }

//    for Setting
    public ItemRequestHolder(Long outletId, Long itemId, String itemName, Boolean isActive) {
        this.outletId = outletId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.isActive = isActive;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
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

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.holder;

/**
 *
 * @author Trisna
 */
public class ItemRequestHolder {

    private Long itemId;
    private Long stock;
    private String itemName;

    public ItemRequestHolder() {
    }

    public ItemRequestHolder(Long itemId, Long stock, String itemName) {
        this.itemId = itemId;
        this.stock = stock;
        this.itemName = itemName;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}

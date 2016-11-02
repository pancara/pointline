/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.holder;

/**
 *
 * @author Arya
 */
public class FavouriteItemsHolder {

    private String itemName;
    private Long quantity;

    public FavouriteItemsHolder() {
    }
    
    public FavouriteItemsHolder(String itemName, Long quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
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

}

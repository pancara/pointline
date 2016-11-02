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
public class SaleItemsDiscountHolder {
     
    private ItemsDiscountHolder itemDiscount;
    
    public SaleItemsDiscountHolder() {
    }

    public ItemsDiscountHolder getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(ItemsDiscountHolder itemDiscount) {
        this.itemDiscount = itemDiscount;
    }
    
}

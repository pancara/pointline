/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.holder;

import java.util.List;

/**
 *
 * @author Arya
 */
public class ItemsHolder {

    private List<AllItemsHolder> allItems;
    
    public ItemsHolder() {
    }
    
    public List<AllItemsHolder> getAllItems() {
        return allItems;
    }

    public void setAllItems(List<AllItemsHolder> allItems) {
        this.allItems = allItems;
    }
    
}

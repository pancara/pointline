/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.holder;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author Arya
 */
public class AllItemsHolder {
    
    private String categoryName;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private Integer itemQuantity;
    
    public AllItemsHolder(){
        
    }
    public AllItemsHolder(String categoryName, Long itemId, String itemCode, String itemName, Integer itemQuantity) {
        this.categoryName = categoryName;
        this.itemCode = itemCode;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }


}

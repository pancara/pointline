/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.holder;

import java.math.BigDecimal;

/**
 *
 * @author Trisna
 */
public class InvoiceItemHolder {

    private Long id;
    private Long itemId;
    private String itemName;
    private BigDecimal itemSupplyPrice;
    private BigDecimal itemSalesPrice;
    private Integer itemQuantity;
    private BigDecimal itemSubTotal;
    private InvoiceItemDiscountHolder itemDiscount;

    public InvoiceItemHolder(Long id, Long itemId, String itemName, BigDecimal itemSupplyPrice, 
            BigDecimal itemSalesPrice, Integer itemQuantity, BigDecimal itemSubTotal) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemSupplyPrice = itemSupplyPrice;
        this.itemSalesPrice = itemSalesPrice;
        this.itemQuantity = itemQuantity;
        this.itemSubTotal = itemSubTotal;
    }
    
    public InvoiceItemHolder(Long id, Long itemId, String itemName, BigDecimal itemSupplyPrice, 
            BigDecimal itemSalesPrice, Integer itemQuantity, BigDecimal itemSubTotal, 
            InvoiceItemDiscountHolder itemDiscount) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemSupplyPrice = itemSupplyPrice;
        this.itemSalesPrice = itemSalesPrice;
        this.itemQuantity = itemQuantity;
        this.itemSubTotal = itemSubTotal;
        this.itemDiscount = itemDiscount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getItemSupplyPrice() {
        return itemSupplyPrice.toPlainString();
    }

    public void setItemSupplyPrice(BigDecimal itemSupplyPrice) {
        this.itemSupplyPrice = itemSupplyPrice;
    }

    public String getItemSalesPrice() {
        return itemSalesPrice.toPlainString();
    }

    public void setItemSalesPrice(BigDecimal itemSalesPrice) {
        this.itemSalesPrice = itemSalesPrice;
    }

    public String getItemQuantity() {
        return String.valueOf(itemQuantity);
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemSubTotal() {
        return itemSubTotal.toPlainString();
    }

    public void setItemSubTotal(BigDecimal itemSubTotal) {
        this.itemSubTotal = itemSubTotal;
    }

    public InvoiceItemDiscountHolder getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(InvoiceItemDiscountHolder itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

}

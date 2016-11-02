/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Trisna
 */
public class MerchantDashboardHolder {

    private BigDecimal totalBill = BigDecimal.ZERO;
    private Double averageBill = 0.0;
    private Long totalInvoice = 0L;
    
    private BigDecimal totalItemsRevenue = BigDecimal.ZERO;
    private BigDecimal totalPricingRevenue = BigDecimal.ZERO;
    private Long totalItems = 0L;
    
    private TopupRevenueHolder topupRevenue = new TopupRevenueHolder();
    
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    
    
    private List<PaymentTypeRevenueHolder> paymentTypeRevenueList;
    private List<CategoryRevenueHolder> categoryRevenueList;
    private List<FavouriteItemsHolder> favouriteItemsList;    
    private List<PaymentTypeRevenueHolder> paymentReversal;
    
    private BigDecimal totalBillReversal = BigDecimal.ZERO;
    private Double averageBillReversal = 0.0;
    private Long totalInvoiceReversal = 0L;
    
    public MerchantDashboardHolder() {
    }
    
    public MerchantDashboardHolder(BigDecimal totalBill, Double averageBill, Long totalInvoice) {
        if (totalBill != null) {
            this.totalBill = totalBill;
        }
        
        if (averageBill != null) {
            this.averageBill = averageBill;
        }
        
        if (totalInvoice != null) {
            this.totalInvoice = totalInvoice;
        }
    }

    public BigDecimal getTotalBill() {
        return totalBill == null ? BigDecimal.ZERO : totalBill;
    }

    public void setTotalBill(BigDecimal totalBill) {
        this.totalBill = totalBill;
    }

    public Double getAverageBill() {
        //return averageBill;
        return averageBill == null ? 0 : averageBill;
    }

    public void setAverageBill(Double averageBill) {
        this.averageBill = averageBill;
    }

    public Long getTotalInvoice() {
        return totalInvoice == null ? (long) 0 : totalInvoice;
    }

    public void setTotalInvoice(Long totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public BigDecimal getTotalItemsRevenue() {
        //return totalItemsRevenue;
        return totalItemsRevenue == null ? BigDecimal.ZERO : totalItemsRevenue;
     
    }

    public void setTotalItemsRevenue(BigDecimal totalItemsRevenue) {
        this.totalItemsRevenue = totalItemsRevenue;
    }

    public BigDecimal getTotalPricingRevenue() {
        return totalPricingRevenue == null ? BigDecimal.ZERO : totalPricingRevenue;
    }

    public void setTotalPricingRevenue(BigDecimal totalPricingRevenue) {
        this.totalPricingRevenue = totalPricingRevenue;
    }

    public Long getTotalItems() {
        return totalItems == null ? (long) 0 : totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    public TopupRevenueHolder getTopupRevenue() {
        return topupRevenue;
    }

    public void setTopupRevenue(TopupRevenueHolder topupRevenue) {
        this.topupRevenue = topupRevenue;
    }

    public List<PaymentTypeRevenueHolder> getPaymentTypeRevenueList() {
        return paymentTypeRevenueList;
    }

    public void setPaymentTypeRevenueList(List<PaymentTypeRevenueHolder> paymentTypeRevenueList) {
        this.paymentTypeRevenueList = paymentTypeRevenueList;
    }

    public List<CategoryRevenueHolder> getCategoryRevenueList() {
        return categoryRevenueList;
    }

    public void setCategoryRevenueList(List<CategoryRevenueHolder> categoryRevenueList) {
        this.categoryRevenueList = categoryRevenueList;
    }

    public List<FavouriteItemsHolder> getFavouriteItemsList() {
        return favouriteItemsList;
    }

    public void setFavouriteItemsList(List<FavouriteItemsHolder> favouriteItemsList) {
        this.favouriteItemsList = favouriteItemsList;
    }

    public BigDecimal getTotalDiscount() {
        //return totalDiscount;
        return totalDiscount == null ? BigDecimal.ZERO : totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public List<PaymentTypeRevenueHolder> getPaymentReversal() {
        return paymentReversal;
    }

    public void setPaymentReversal(List<PaymentTypeRevenueHolder> paymentReversal) {
        this.paymentReversal = paymentReversal;
    }

    public BigDecimal getTotalBillReversal() {
        return totalBillReversal;
    }

    public void setTotalBillReversal(BigDecimal totalBillReversal) {
        this.totalBillReversal = totalBillReversal;
    }

    public Double getAverageBillReversal() {
        return averageBillReversal;
    }

    public void setAverageBillReversal(Double averageBillReversal) {
        this.averageBillReversal = averageBillReversal;
    }

    public Long getTotalInvoiceReversal() {
        return totalInvoiceReversal;
    }

    public void setTotalInvoiceReversal(Long totalInvoiceReversal) {
        this.totalInvoiceReversal = totalInvoiceReversal;
    }

}

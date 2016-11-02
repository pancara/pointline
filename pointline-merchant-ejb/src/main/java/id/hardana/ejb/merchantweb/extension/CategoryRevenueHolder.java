/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author Trisna
 */
public class CategoryRevenueHolder {

    private String categoryName;
    private BigDecimal revenue;

    public CategoryRevenueHolder(String categoryName, BigDecimal revenue) {
        this.categoryName = categoryName;
        this.revenue = revenue;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CategoryRevenueHolder other = (CategoryRevenueHolder) obj;
        if (!Objects.equals(this.categoryName, other.categoryName)) {
            return false;
        }
        return true;
    }

}

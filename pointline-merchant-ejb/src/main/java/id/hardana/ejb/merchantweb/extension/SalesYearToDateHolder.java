/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import java.math.BigDecimal;
/**
 *
 * @author Arya
 */

public class SalesYearToDateHolder {

    public String month;
    public BigDecimal totalSale;    
    public BigDecimal totalTopup;
    
    public SalesYearToDateHolder() 
    {
    }
    
    public BigDecimal getTotalTopup() {
        return totalTopup;
    }

    public void setTotalTopup(BigDecimal totalTopup) {
        this.totalTopup = totalTopup;
    }

    public BigDecimal getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(BigDecimal totalSale) {
        this.totalSale = totalSale;
    }
 
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

}

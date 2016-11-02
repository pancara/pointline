/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import java.math.BigDecimal;
import java.util.Date;
/**
 *
 * @author Arya
 */

public class SalesMonthlyByDateHolder {

    public int date;
    public BigDecimal totalSale;    
    public BigDecimal totalTopup;
    
    public SalesMonthlyByDateHolder() 
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

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
    

}

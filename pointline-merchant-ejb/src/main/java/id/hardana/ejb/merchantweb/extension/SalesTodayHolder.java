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

public class SalesTodayHolder {

    public String hour;
    public BigDecimal totalSale;    
    public BigDecimal totalTopup;
    
    public SalesTodayHolder() 
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

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

   
 

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import java.util.List;

/**
 *
 * @author Arya
 */
public class MerchantWebHolderSalesYearToDate {     
    private List<SalesYearToDateHolder> salesYearToDateList;

    public MerchantWebHolderSalesYearToDate()
    {
    }
  
    public List<SalesYearToDateHolder> getYearToDateList() {       
        return salesYearToDateList;
    }
    
    public void setYearToDateList(List<SalesYearToDateHolder> salesYearToDateList) {       
        this.salesYearToDateList = salesYearToDateList;
    }
    
}

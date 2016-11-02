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
public class MerchantWebHolderSalesMonthToDate {     
    private List<SalesMonthlyByDateHolder> salesMonthToDateList;

    public MerchantWebHolderSalesMonthToDate()
    {
    }
  
    public List<SalesMonthlyByDateHolder> getMonthToDateList() {       
        return salesMonthToDateList;
    }
    
    public void setMonthToDateList(List<SalesMonthlyByDateHolder> salesMonthToDateList) {       
        this.salesMonthToDateList = salesMonthToDateList;
    }
    
}

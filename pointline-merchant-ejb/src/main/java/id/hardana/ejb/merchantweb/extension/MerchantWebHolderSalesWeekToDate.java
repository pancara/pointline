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
public class MerchantWebHolderSalesWeekToDate {     
    private List<SalesWeekToDateHolder> salesWeekToDateList;

    public MerchantWebHolderSalesWeekToDate()
    {
    }

    public List<SalesWeekToDateHolder> getWeekToDateList() {
        return salesWeekToDateList;
    }

    public void setWeekToDateList(List<SalesWeekToDateHolder> salesWeekToDateList) {
        this.salesWeekToDateList = salesWeekToDateList;
    }
 
}

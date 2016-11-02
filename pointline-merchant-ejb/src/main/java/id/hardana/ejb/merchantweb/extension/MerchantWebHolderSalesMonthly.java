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
public class MerchantWebHolderSalesMonthly {     
    private List<SalesMonthlyByDateHolder> salesMonthlyList;

    public MerchantWebHolderSalesMonthly()
    {
    }

    public List<SalesMonthlyByDateHolder> getMonthlyList() {
        return salesMonthlyList;
    }

    public void setMonthlyList(List<SalesMonthlyByDateHolder> salesMonthlyList) {
        this.salesMonthlyList = salesMonthlyList;
    }
 
}

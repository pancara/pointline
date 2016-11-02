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
public class MerchantWebHolderSalesToday {     
    private List<SalesTodayHolder> salesTodayList;

    public MerchantWebHolderSalesToday()
    {
    }

    public List<SalesTodayHolder> getTodayList() {
        return salesTodayList;
    }

    public void setTodayList(List<SalesTodayHolder> salesTodayList) {
        this.salesTodayList = salesTodayList;
    }
 
}

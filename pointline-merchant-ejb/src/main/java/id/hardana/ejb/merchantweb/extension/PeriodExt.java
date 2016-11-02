/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author hanafi
 */
public class PeriodExt {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    //private long id;

    private Object period;
    private BigDecimal sumSales;
    private BigDecimal sumTopup;
    private BigDecimal sumTotalAmountTopup;
    
    public PeriodExt(){
    }
    
    public PeriodExt(Object period, BigDecimal sumSales){
        //this.id = id;
        this.period = period;
        this.sumSales = sumSales;
        this.sumTopup = sumTopup;
    }

    public PeriodExt(Object period, BigDecimal sumTopup, BigDecimal sumTotalAmountTopup){
        
        this.period = period;
        this.sumTopup = sumTopup;
        this.sumTotalAmountTopup = sumTotalAmountTopup;
    } 
    
    /**
     * @return the period
     */
    public String getPeriod() {
        return period.toString();
    }

    /**
     * @param period the period to set
     */
    public void setPeriod(Date period) {
        this.period = period;
    }

    /**
     * @return the sum
     */
    public String getSumSales() {
        return sumSales.toString();
    }

    /**
     * @param sum the sum to set
     */
    public void setSumSales(BigDecimal sumSales) {
        this.sumSales = sumSales;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    /**
     * @return the sumTopup
     */
    public String getSumTopup() {
        return sumTopup.toString();
    }

    /**
     * @param sumTopup the sumTopup to set
     */
    public void setSumTopup(BigDecimal sumTopup) {
        this.sumTopup = sumTopup;
    }
}

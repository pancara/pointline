/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.holder;

import java.math.BigDecimal;

/**
 *
 * @author Arya
 */
public class TopupRevenueHolder {

    private Long frequency;
    private BigDecimal amount;
    
    public TopupRevenueHolder(){
    }

    public TopupRevenueHolder(Long freq, BigDecimal amount) {
        this.frequency = freq;
        this.amount = amount;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}

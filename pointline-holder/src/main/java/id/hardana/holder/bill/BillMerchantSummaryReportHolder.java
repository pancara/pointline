/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.bill;

import java.math.BigDecimal;

/**
 *
 * @author Trisna
 */
public class BillMerchantSummaryReportHolder {

    private Long numberOfBill;
    private Long numberOfOpenBill;
    private Long numberOfClosedBill;
    private BigDecimal amountOfBill;
    private BigDecimal amountOfOpenBill;
    private BigDecimal amountOfClosedBill;
    private Double percentageOfNumberOpenBill;
    private Double percentageOfNumberClosedBill;
    private Double percentageOfAmountOpenBill;
    private Double percentageOfAmountClosedBill;

    public BillMerchantSummaryReportHolder() {
    }

    public Long getNumberOfBill() {
        return numberOfBill;
    }

    public void setNumberOfBill(Long numberOfBill) {
        this.numberOfBill = numberOfBill;
    }

    public Long getNumberOfOpenBill() {
        return numberOfOpenBill;
    }

    public void setNumberOfOpenBill(Long numberOfOpenBill) {
        this.numberOfOpenBill = numberOfOpenBill;
    }

    public Long getNumberOfClosedBill() {
        return numberOfClosedBill;
    }

    public void setNumberOfClosedBill(Long numberOfClosedBill) {
        this.numberOfClosedBill = numberOfClosedBill;
    }

    public BigDecimal getAmountOfBill() {
        return amountOfBill;
    }

    public void setAmountOfBill(BigDecimal amountOfBill) {
        this.amountOfBill = amountOfBill;
    }

    public BigDecimal getAmountOfOpenBill() {
        return amountOfOpenBill;
    }

    public void setAmountOfOpenBill(BigDecimal amountOfOpenBill) {
        this.amountOfOpenBill = amountOfOpenBill;
    }

    public BigDecimal getAmountOfClosedBill() {
        return amountOfClosedBill;
    }

    public void setAmountOfClosedBill(BigDecimal amountOfClosedBill) {
        this.amountOfClosedBill = amountOfClosedBill;
    }

    public Double getPercentageOfNumberOpenBill() {
        return percentageOfNumberOpenBill;
    }

    public void setPercentageOfNumberOpenBill(Double percentageOfNumberOpenBill) {
        this.percentageOfNumberOpenBill = percentageOfNumberOpenBill;
    }

    public Double getPercentageOfNumberClosedBill() {
        return percentageOfNumberClosedBill;
    }

    public void setPercentageOfNumberClosedBill(Double percentageOfNumberClosedBill) {
        this.percentageOfNumberClosedBill = percentageOfNumberClosedBill;
    }

    public Double getPercentageOfAmountOpenBill() {
        return percentageOfAmountOpenBill;
    }

    public void setPercentageOfAmountOpenBill(Double percentageOfAmountOpenBill) {
        this.percentageOfAmountOpenBill = percentageOfAmountOpenBill;
    }

    public Double getPercentageOfAmountClosedBill() {
        return percentageOfAmountClosedBill;
    }

    public void setPercentageOfAmountClosedBill(Double percentageOfAmountClosedBill) {
        this.percentageOfAmountClosedBill = percentageOfAmountClosedBill;
    }

}

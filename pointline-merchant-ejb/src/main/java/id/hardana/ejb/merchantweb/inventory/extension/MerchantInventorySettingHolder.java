/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory.extension;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Trisna
 */
public class MerchantInventorySettingHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String merchantCode;
    private String merchantName;
    private String updatedBy;
    private Date lastUpdated;
    private Boolean isActiveInventory;

    public MerchantInventorySettingHolder(String merchantCode, String merchantName, String updatedBy,
            Date lastUpdated, Boolean isActiveInventory) {
        this.merchantCode = merchantCode;
        this.merchantName = merchantName;
        this.updatedBy = updatedBy;
        this.lastUpdated = lastUpdated;
        this.isActiveInventory = isActiveInventory;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getLastUpdated() {
        return lastUpdated == null ? null : DATE_FORMAT.format(lastUpdated);
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getIsActiveInventory() {
        return isActiveInventory == null ? "false" : Boolean.toString(isActiveInventory);
    }

    public void setIsActiveInventory(Boolean isActiveInventory) {
        this.isActiveInventory = isActiveInventory;
    }

}

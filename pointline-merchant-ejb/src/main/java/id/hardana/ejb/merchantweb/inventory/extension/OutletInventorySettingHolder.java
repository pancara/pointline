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
public class OutletInventorySettingHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Long outletId;
    private String outletName;
    private String updatedBy;
    private Date lastUpdated;
    private Boolean isActiveInventory;

    public OutletInventorySettingHolder(Long outletId, String outletName, String updatedBy,
            Date lastUpdated, Boolean isActiveInventory) {
        this.outletId = outletId;
        this.outletName = outletName;
        this.updatedBy = updatedBy;
        this.lastUpdated = lastUpdated;
        this.isActiveInventory = isActiveInventory;
    }

    public String getOutletId() {
        return outletId == null ? null : String.valueOf(outletId);
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
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

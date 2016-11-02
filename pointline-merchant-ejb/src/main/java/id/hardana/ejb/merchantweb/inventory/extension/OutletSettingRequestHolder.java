/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory.extension;

/**
 *
 * @author Trisna
 */
public class OutletSettingRequestHolder {

    private Long outletId;
    private Boolean isActive;

    public OutletSettingRequestHolder(Long outletId, Boolean isActive) {
        this.outletId = outletId;
        this.isActive = isActive;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.holder.bill;

import id.hardana.entity.bill.enums.BillStatusEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;

/**
 *
 * @author Trisna
 */
public class BillHolder {

    private String account;
    private String billNumber;
    private boolean approve;
    private ResponseStatusEnum status;
    private BillStatusEnum billStatus;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public boolean isApprove() {
        return approve;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    public ResponseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusEnum status) {
        this.status = status;
    }

    public BillStatusEnum getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(BillStatusEnum billStatus) {
        this.billStatus = billStatus;
    }

}

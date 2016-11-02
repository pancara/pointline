/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.mdb;

import java.io.Serializable;

/**
 *
 * @author Trisna
 */
public class MailSystemObject implements Serializable {

    private String to;
    private MailSystemType mailType;
//   Merchant Registration 
    private String userName;
    private String merchantName;
    private String merchantCode;
//    Personal Forget Password
    private String firstName;
    private String lastName;
    private String account;
    private String newPassword;
    private String gmUserName;
    private String gmName;
    private String gmCode;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public MailSystemType getMailType() {
        return mailType;
    }

    public void setMailType(MailSystemType mailType) {
        this.mailType = mailType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getGmUserName() {
        return gmUserName;
    }

    public void setGmUserName(String gmUserName) {
        this.gmUserName = gmUserName;
    }

    public String getGmName() {
        return gmName;
    }

    public void setGmName(String gmName) {
        this.gmName = gmName;
    }

    public String getGmCode() {
        return gmCode;
    }

    public void setGmCode(String gmCode) {
        this.gmCode = gmCode;
    }

    
}

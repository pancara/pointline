/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

/**
 *
 * @author Arya
 */
public class GroupMerchantHolder {

    private Long groupId;
    private String groupName;
    private String ownerEmail;
    
    public GroupMerchantHolder() {
    }

    public GroupMerchantHolder(Long groupId, String groupName, String ownerEmail) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.ownerEmail = ownerEmail;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getEmail() {
        return ownerEmail;
    }

    public void setEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

}

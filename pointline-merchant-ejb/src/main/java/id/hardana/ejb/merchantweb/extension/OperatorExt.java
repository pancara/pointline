/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.extension;

import id.hardana.entity.profile.merchant.Operator;
import javax.persistence.Entity;

/**
 *
 * @author hanafi
 */
public class OperatorExt extends Operator {
    
    private String operatorLevelName;

    public OperatorExt(){
        super();
    }
    /**
     * @return the operatorLevelName
     */
    public String getOperatorLevelName() {
        return operatorLevelName;
    }

    /**
     * @param operatorLevelName the operatorLevelName to set
     */
    public void setOperatorLevelName(String operatorLevelName) {
        this.operatorLevelName = operatorLevelName;
    }
    
    
}

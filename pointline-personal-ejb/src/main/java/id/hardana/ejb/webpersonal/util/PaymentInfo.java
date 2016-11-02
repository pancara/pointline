 package id.hardana.ejb.webpersonal.util;

import java.io.Serializable;

/**
 *
 * @author pancara
 */
public class PaymentInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String field;
    private String value;

    public PaymentInfo() {
    }

    public PaymentInfo(String field, String value) {
        this.field = field;
        this.value = value;
    }
 
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    

    
}

package id.hardana.ejb.webpersonal.util;

import java.io.Serializable;

/**
 * Created by pancara on 7/11/16.
 */
public class ChartTableItem implements Serializable{
    private static final long serialVersionUID = 1L;

    private String denomination;
    private String price;

    public ChartTableItem() {
    }

    public ChartTableItem(String denomination, String price) {
        this.denomination = denomination;
        this.price = price;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ChartTableItem{" + "denomination=" + denomination + ", price=" + price + '}';
    }

    
    
}

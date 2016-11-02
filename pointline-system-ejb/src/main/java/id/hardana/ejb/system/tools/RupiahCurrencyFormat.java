/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.tools;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author arya
 *
 */
public class RupiahCurrencyFormat {

    public static String toRupiahFormat(double nominal) {
        boolean isNegative = false;
        if (nominal < 0) {
            isNegative = true;
        }
        String prefix = "Rp. ";
        if (isNegative) {
            prefix = "-Rp. ";
        }

        String rupiah = prefix + toFormat(nominal);
        return rupiah;
    }
    
    public static String toNonRupiahFormat(double nominal) {
        boolean isNegative = false;
        if (nominal < 0) {
            isNegative = true;
        }
        String prefix = "";
        if (isNegative) {
            prefix = "-";
        }

        String rupiah = prefix + toFormat(nominal);
        return rupiah;
    }

    private static String toFormat(double nominal) {
        String format = "";

        Locale locale = new Locale("ca", "CA");
        NumberFormat frmt = NumberFormat.getCurrencyInstance(locale);

        format = frmt.format(nominal).substring(4);
        return format;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.atv.pointline.misc;

import java.text.NumberFormat;
import java.util.Locale;

public class RupiahFormatter {

    private static NumberFormat formatter;

    static {
        NumberFormat.getCurrencyInstance(new Locale("en", "EN"));
    }

    public static String format(double value) {
        return formatter.format(value).substring(4);
    }

    public static String toRupiahFormat(String nominal) {
        return format(Double.parseDouble(nominal));
    }

//    public static String toRupiahFormat(double nominal) {
//        boolean isNegative = false;
//        if (nominal < 0) {
//            isNegative = true;
//        }
//        String prefix = "Rp. ";
//        if (isNegative) {
//            prefix = "-Rp. ";
//        }
//
//        String rupiah = prefix + toFormat(nominal);
//        return rupiah;
//    }
//
//    public static String toNonRupiahFormat(double nominal) {
//        boolean isNegative = false;
//        if (nominal < 0) {
//            isNegative = true;
//        }
//        String prefix = "";
//        if (isNegative) {
//            prefix = "-";
//        }
//
//        String rupiah = prefix + toFormat(nominal);
//        return rupiah;
//    }


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.tools;

/**
 *
 * @author Trisna
 */
public class PhoneNumberVadalidator {

    public static String formatPhoneNumber(String account) {
        if (account.startsWith("+62")) {
            account = account.substring(1);
        } else if (account.startsWith("0")) {
            account = "62" + account.substring(1);
        } else if (account.startsWith("62")) {
        } else {
            return null;
        }

        if (account.matches("[0-9]+") && account.length() > 9 && account.length() < 17) {
            return account;
        } else {
            return null;
        }
    }

}

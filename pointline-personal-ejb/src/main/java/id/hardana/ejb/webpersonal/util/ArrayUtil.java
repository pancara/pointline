/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.util;

/**
 *
 * @author pancara
 */
public class ArrayUtil {

    public static boolean contains(Object[] collection, Object element) {
        for (Object o : collection) {
            if (o.equals(element)) {
                return true;
            }
        }
        return false;
    }
}

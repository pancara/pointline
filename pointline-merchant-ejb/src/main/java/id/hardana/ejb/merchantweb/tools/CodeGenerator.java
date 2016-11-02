/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Trisna
 */
public class CodeGenerator {

    public static String generatePassword() {
        String ALPHA_NUM = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnpqrtuvwxyz";
        String SYMBOL = ".,?!@#$%^&*()-_=+/";
        StringBuilder sbAn = new StringBuilder(10);
        StringBuilder sbSy = new StringBuilder(2);
        
        for (int i = 0; i < 10; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sbAn.append(ALPHA_NUM.charAt(ndx));
        }
        for (int i = 0; i < 2; i++) {
            int ndx = (int) (Math.random() * SYMBOL.length());
            sbSy.append(SYMBOL.charAt(ndx));
        }

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int random1 = random.nextInt(5) + 1;
        int random2 = random.nextInt(5) + 6;
        sb.append(sbAn.substring(0, random1))
                .append(sbSy.substring(0, 1))
                .append(sbAn.substring(random1, random2))
                .append(sbSy.substring(1))
                .append(sbAn.substring(random2));
        return sb.toString();
    }

    public static String generateUID() {
        String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(16);
        
        for (int i = 0; i < 16; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }
        return sb.toString();
    }

    public static String generateMerchantCode() {
        String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }

        return sb.toString();
    }
    
    public static String generateGroupCode() {
        String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(6);
        
        for (int i = 0; i < 6; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }
        return sb.toString();
    }

    public static String generateSessionId() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        return sessionId;
    }

    public static String generateToken() {
        String token = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(5, 21);
        return token;
    }

    public static String generateReferenceNumber() {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyy");
        String dateString = DATE_FORMAT.format(new Date());
        String ALPHA_NUM = "0123456789";
        StringBuilder sb = new StringBuilder(10);
        
        for (int i = 0; i < 10; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }
        return dateString + sb.toString();
    }
}

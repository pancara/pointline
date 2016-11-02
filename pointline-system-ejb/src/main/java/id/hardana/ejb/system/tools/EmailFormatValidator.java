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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailFormatValidator {

    private static Pattern pattern;
    private static Matcher matcher;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean validate(final String email) {
        if (pattern == null) {
            pattern = Pattern.compile(EMAIL_PATTERN);
        }
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

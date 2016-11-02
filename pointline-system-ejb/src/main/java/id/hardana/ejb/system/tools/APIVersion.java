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
public class APIVersion {

    private final static int apiVersionMerchantWeb = 106;
    private final static int apiVersionPersonalweb = 106;
    private final static int apiVersionMPOS = 106;
    private final static int apiVersionPersonal = 106;
    private final static int apiVersionAdmin = 106;
    private final static int minimumApiVersionMerchantWeb = 0;
    private final static int minimumApiVersionPersonalWeb = 0;
    private final static int minimumApiVersionMPOS = 0;
    private final static int minimumApiVersionPersonal = 0;
    private final static int minimumApiVersionAdmin = 0;

    public static int getApiVersionMerchantWeb() {
        return apiVersionMerchantWeb;
    }

    public static int getApiVersionPersonalweb() {
        return apiVersionPersonalweb;
    }

    public static int getApiVersionMPOS() {
        return apiVersionMPOS;
    }

    public static int getApiVersionPersonal() {
        return apiVersionPersonal;
    }

    public static int getApiVersionAdmin() {
        return apiVersionAdmin;
    }

    public static int getMinimumApiVersionMerchantWeb() {
        return minimumApiVersionMerchantWeb;
    }

    public static int getMinimumApiVersionPersonalWeb() {
        return minimumApiVersionPersonalWeb;
    }

    public static int getMinimumApiVersionMPOS() {
        return minimumApiVersionMPOS;
    }

    public static int getMinimumApiVersionPersonal() {
        return minimumApiVersionPersonal;
    }

    public static int getMinimumApiVersionAdmin() {
        return minimumApiVersionAdmin;
    }

}

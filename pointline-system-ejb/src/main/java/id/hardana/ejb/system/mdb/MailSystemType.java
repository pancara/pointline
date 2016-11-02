/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.mdb;

/**
 *
 * @author Trisna
 */
public enum MailSystemType {

    MERCHANT_REGISTRATION(0, "Merchant Code"),
    PERSONAL_FORGET_PASSWORD(1, "Reset Password"),
    OP_MERCHANT_FORGET_PASSWORD(2, "Reset Password"),
    GROUP_MERCHANT_REGISTRATION(3, "Group Code");
    ;

    private int mailTypeId;
    private String mailType;
    private String subject;

    private MailSystemType(int mailTypeId, String subject) {
        this.mailTypeId = mailTypeId;
        this.mailType = name();
        this.subject = subject;
    }

    public int getMailTypeId() {
        return mailTypeId;
    }

    public String getMailType() {
        return mailType;
    }

    public String getSubject() {
        return subject;
    }

}

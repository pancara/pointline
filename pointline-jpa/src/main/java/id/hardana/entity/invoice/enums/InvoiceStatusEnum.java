/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.invoice.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trisna
 */
public enum InvoiceStatusEnum {

    PAID(0),
    PENDING(1),
    CANCELED(2),
    UNFINISHED(3),
    REVERSAL(4);

    private int invoiceStatusId;
    private String invoiceStatus;

    private static Map<Integer, InvoiceStatusEnum> codeToInvoiceStatusMapping;

    private InvoiceStatusEnum(int invoiceStatusId) {
        this.invoiceStatusId = invoiceStatusId;
        this.invoiceStatus = name();
    }

    public static InvoiceStatusEnum geInvoiceStatus(int i) {
        if (codeToInvoiceStatusMapping == null) {
            initMapping();
        }
        return codeToInvoiceStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToInvoiceStatusMapping = new HashMap<Integer, InvoiceStatusEnum>();
        for (InvoiceStatusEnum m : values()) {
            codeToInvoiceStatusMapping.put(m.invoiceStatusId, m);
        }
    }

    public String getInvoiceStatusId() {
        return String.valueOf(invoiceStatusId);
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }
}

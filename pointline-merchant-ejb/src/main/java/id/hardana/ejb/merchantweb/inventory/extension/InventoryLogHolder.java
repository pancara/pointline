/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.inventory.extension;

import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Trisna
 */
public class InventoryLogHolder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Long id;
    private InventoryLogTypeEnum type;
    private String outletName;
    private String operatorName;
    private Date dateTime;
    private Date clientDateTime;
    private String description;
    private List<InventoryLogDetailHolder> inventoryLogDetailHolder;

    public InventoryLogHolder(Long id, InventoryLogTypeEnum type, String outletName, String operatorName,
            Date dateTime, Date clientDateTime, String description) {
        this.id = id;
        this.type = type;
        this.outletName = outletName;
        this.operatorName = operatorName;
        this.dateTime = dateTime;
        this.clientDateTime = clientDateTime;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InventoryLogTypeEnum getType() {
        return type;
    }

    public void setType(InventoryLogTypeEnum type) {
        this.type = type;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getDateTime() {
        return DATE_FORMAT.format(dateTime);
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getClientDateTime() {
        return DATE_FORMAT.format(clientDateTime);
    }

    public void setClientDateTime(Date clientDateTime) {
        this.clientDateTime = clientDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InventoryLogDetailHolder> getInventoryLogDetailHolder() {
        return inventoryLogDetailHolder;
    }

    public void setInventoryLogDetailHolder(List<InventoryLogDetailHolder> inventoryLogDetailHolder) {
        this.inventoryLogDetailHolder = inventoryLogDetailHolder;
    }

}

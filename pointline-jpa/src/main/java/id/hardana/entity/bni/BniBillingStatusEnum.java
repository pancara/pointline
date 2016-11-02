package id.hardana.entity.bni;

/**
 * Created by pancara on 1/26/16.
 */
public enum BniBillingStatusEnum {
    PREPARED(0),
    ACTIVE(1),
    NOT_ACTIVE(2);

    private Integer id;

    private BniBillingStatusEnum(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdAsString() {
        return String.valueOf(this.id);
    }

    public static BniBillingStatusEnum valueById(Integer id) {
        for (BniBillingStatusEnum value : values()) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        return null;
    }
}

package id.hardana.entity.bni;

/**
 * Created by pancara on 2/2/16.
 */
public enum BniPaymentStatusEnum {
    NEW(0),
    PERSONAL_TOPUP_SUCCESS(1),
    INVALID_TRANSACTION(2),
    DUPlICATE_PAYMENT_NOTIFICATION(3);

    private Integer id;

    private BniPaymentStatusEnum(Integer id) {
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

    public static BniPaymentStatusEnum valueById(Integer id) {
        for (BniPaymentStatusEnum value : values()) {
            if (value.getId().equals(id)) {
                return value;
            }
        }

        return null;
    }
}

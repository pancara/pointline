package id.hardana.entity.bni;

/**
 * Created by pancara on 2/2/16.
 */
public enum BniPaymentResponseEnum {
    SUCCESS(0),
    TRANSACTION_NOT_FOUND(1),
    TRANSACTION_PAID(2);

    private Integer id;

    private BniPaymentResponseEnum(Integer id) {
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

    public static BniPaymentResponseEnum valueById(Integer id) {
        for (BniPaymentResponseEnum value : values()) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        return null;
    }
}

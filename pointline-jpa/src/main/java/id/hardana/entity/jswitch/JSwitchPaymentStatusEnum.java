package id.hardana.entity.jswitch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pancara on 7/11/16.
 */
public enum JSwitchPaymentStatusEnum {
    PREPARE(0),
    TRX_PAYMENT_RECORD_CREATED(1), // emo transaction payment n invoce created, but before send to jswitch
    SUCCESS(2), // success processed by jswitch
    JSWITCH_FAIL(3);

    private int id;
    private String description;

    private static Map<Integer, JSwitchPaymentStatusEnum> cache;

    JSwitchPaymentStatusEnum(int id) {
        this.id = id;
        this.description = name();
    }

    public static JSwitchPaymentStatusEnum getById(Integer id) {
        if (cache == null) {
            initMapping();
        }
        return cache.get(id);
    }

    private static void initMapping() {
        cache = new HashMap<>();
        for (JSwitchPaymentStatusEnum e : values()) {
            cache.put(e.getId(), e);
        }
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}

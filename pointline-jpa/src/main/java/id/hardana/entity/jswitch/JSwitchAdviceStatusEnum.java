package id.hardana.entity.jswitch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pancara on 7/11/16.
 */
public enum JSwitchAdviceStatusEnum {
    REQUEST(0),
    SUCCESS(1),
    REQUEST_FAIL(2),
    FAIL(3);

    private int id;
    private String description;

    private static Map<Integer, JSwitchAdviceStatusEnum> cache;

    JSwitchAdviceStatusEnum(int id) {
        this.id = id;
        this.description = name();
    }

    public static JSwitchAdviceStatusEnum getById(Integer id) {
        if (cache == null) {
            initMapping();
        }
        return cache.get(id);
    }

    private static void initMapping() {
        cache = new HashMap<>();
        for (JSwitchAdviceStatusEnum e : values()) {
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

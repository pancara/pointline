package id.hardana.entity.jswitch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pancara on 7/11/16.
 */
public enum JSwitchInquiryStatusEnum {
    INQUIRY_REQUEST(0),
    INQUIRY_SUCCESS(1),
    INQUIRY_INVALID(2),
    INQUIRY_REQUEST_FAIL(3);

    private int id;
    private String description;

    private static Map<Integer, JSwitchInquiryStatusEnum> cache;

    private JSwitchInquiryStatusEnum(int id) {
        this.id = id;
        this.description= name();
    }

    public static JSwitchInquiryStatusEnum getById(Integer id) {
        if (cache == null) {
            initMapping();
        }
        return cache.get(id);
    }

    private static void initMapping() {
        cache = new HashMap<>();
        for (JSwitchInquiryStatusEnum e : values()) {
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

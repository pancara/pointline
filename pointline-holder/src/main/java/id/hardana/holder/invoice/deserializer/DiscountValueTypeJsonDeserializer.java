/**
 * 
 * @Author pancara
 * 
 */
package id.hardana.holder.invoice.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;
import java.lang.reflect.Type;

/**
 *
 * @author pancara
 */
public class DiscountValueTypeJsonDeserializer implements JsonDeserializer<DiscountValueTypeEnum> {

    @Override
    public DiscountValueTypeEnum deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        int key = element.getAsInt();
        return DiscountValueTypeEnum.getValue(key);
    }

}

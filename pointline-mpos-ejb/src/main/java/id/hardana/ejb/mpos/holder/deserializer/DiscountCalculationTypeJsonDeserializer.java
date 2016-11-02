/**
 * 
 * @Author pancara
 * 
 */

package id.hardana.ejb.mpos.holder.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import java.lang.reflect.Type;

/**
 *
 * @author pancara
 */
public class DiscountCalculationTypeJsonDeserializer implements JsonDeserializer<DiscountCalculationTypeEnum> {

    @Override
    public DiscountCalculationTypeEnum deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        int key = element.getAsInt();
        return DiscountCalculationTypeEnum.getValue(key);
    }

}

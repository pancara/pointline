/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal.interceptor;

import id.hardana.entity.sys.enums.ResponseStatusEnum;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.json.JSONObject;

/**
 *
 * @author trisna
 */
public class NullCheckInterceptor {

    private final String STATUS_KEY = "status";

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {

        try {
            Object[] ob = ctx.getParameters();

            if (ob != null || ob.length != 0) {
                for (Object ob1 : ob) {
                    if (ob1 == null || ob1.equals("")) {
                        JSONObject response = new JSONObject();
                        response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
                        return response.toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        Object result = ctx.proceed();
        return result;
    }
}

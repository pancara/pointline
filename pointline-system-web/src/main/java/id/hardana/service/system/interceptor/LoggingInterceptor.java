/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system.interceptor;

import id.hardana.service.system.log.LogUtil;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author trisna
 */
public class LoggingInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {

        String methodName = ctx.getMethod().getName();

        StringBuilder sb = new StringBuilder();
        try {
            Object[] ob = ctx.getParameters();

            if (ob != null || ob.length != 0) {
                for (Object ob1 : ob) {
                    sb.append(ob1).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (Exception e) {
        }

        Object result = ctx.proceed();
        LogUtil.stream(methodName.toUpperCase() + " REQUEST : " + sb.toString());
        LogUtil.stream(methodName.toUpperCase() + " RESPOND : " + result.toString());

        return result;
    }
}

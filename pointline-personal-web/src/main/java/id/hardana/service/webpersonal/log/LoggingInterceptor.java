/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal.log;

import id.hardana.ejb.system.log.SystemLog;
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

        SystemLog.getInstance().logStream(methodName + " REQUEST : " + methodName + "(" + sb.toString() + ")");
        Object result = ctx.proceed();
        SystemLog.getInstance().logStream(methodName + " RESPOND : " + result.toString() + ". FOR REQUEST : " + methodName + "(" + sb.toString() + ")");

        return result;
    }
}

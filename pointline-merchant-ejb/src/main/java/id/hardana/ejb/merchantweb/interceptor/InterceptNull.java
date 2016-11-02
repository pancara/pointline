/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.interceptor;

import java.util.HashMap;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.json.JSONObject;

/**
 *
 * @author hanafi
 */
public class InterceptNull {
    
    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception{
        
        String methodName = ctx.getMethod().getName();
        Object[] ob = ctx.getParameters();
        StringBuilder sb = new StringBuilder();
        Boolean somethingIsNull = false;
        
        System.out.println("intercepted method name: " + methodName);
        
        //System.out.println(ob.toString());
        
        if(ob == null || ob.length <= 0){
        } else {
            for (Object ob1 :ob) {
                //System.out.println(ob1.toString());
                if (ob1 == null || ob1.toString().isEmpty()) {
                    HashMap<String,String> retVal = new HashMap<>();
                    retVal.put("status", "SOMETHING IS NULL");
                    return new JSONObject(retVal).toString();
                    //return "SOMETHING IS NULL";
                }
            }
        }
        
        Object result = ctx.proceed();
    
        return result;
    } 
}

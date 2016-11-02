/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.tools;

import java.util.Iterator;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
public class JSONTools {

    public static JSONObject mergeJSONObject(JSONObject o1, JSONObject o2) {
        JSONObject mergedObj = new JSONObject();
        Iterator i1 = o1.keys();
        Iterator i2 = o2.keys();
        String tmp_key;
        while (i1.hasNext()) {
            tmp_key = (String) i1.next();
            mergedObj.put(tmp_key, o1.get(tmp_key));
        }
        while (i2.hasNext()) {
            tmp_key = (String) i2.next();
            mergedObj.put(tmp_key, o2.get(tmp_key));
        }
        return mergedObj;
    }
}

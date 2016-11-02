/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.reportdoc;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.io.File;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Arya
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class DeleteFileConfigBean {

    private final String STATUS_KEY = "status";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    
    public JSONObject deleteFileConfigData(String filePath, String fileName) {
        
        JSONObject response = new JSONObject();
        try{
    		File file = new File(filePath +"/" + fileName);
 
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
 
    	}catch(Exception e){
    	}
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        
        return response;
    }

}

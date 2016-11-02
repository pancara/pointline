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

public class ListFileConfigBean {

    private final String STATUS_KEY = "status";
    private final String DATA_KEY = "ListofFileData";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    
    public JSONObject listFileConfigData(String filePath) {
        
        JSONObject response = new JSONObject();
        System.out.println("filePath : " + filePath);
        File fileToList;   
        fileToList = new File(filePath);
        String[] listOfFile = fileToList.list();
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, listOfFile);
        System.out.println("list of file" + response.toString());
        return response;
    }

}

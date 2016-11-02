/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.reportdoc;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

public class DownloadFileConfigBean {

    private final String STATUS_KEY = "status";
    private final String DATA_KEY = "ListOfData";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    
    public JSONObject downloadFileConfigData(String filePath, String fileName) {
        
        JSONObject response = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        String content = "";
        try {
            content = readFile(filePath +"/" + fileName, StandardCharsets.UTF_8);
            jsonObj = new JSONObject(content);
        } catch (IOException ex) {
        }
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, jsonObj);
        return response;
    }
    
    static String readFile(String path, Charset encoding) 
    throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}

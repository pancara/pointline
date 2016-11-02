/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos.servlet;

import id.hardana.ejb.mpos.reportdoc.UploadFileConfigBean;
import id.hardana.ejb.system.validation.OperatorMerchantValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.JSONObject;
import java.util.HashMap;
import javax.ejb.EJB;

/**
 * @author Arya
 */
@WebServlet(name = "UploadFileConfig", urlPatterns = {"/emo/uploadfileconfig"})
@MultipartConfig
public class UploadFileConfig extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private String pathOfConfigFile;
    private final String dirConfig = "mposconfig";
    private final String APP_KEY = "applicationKey";
    private final String MERCHANT_CODE_KEY = "merchantCode";
    private final String OUTLET_ID_KEY = "outletId";
    private final String USER_NAME_KEY = "userName";
    private final String SESSION_KEY = "sessionId";
    private final String TOKEN_KEY = "token"; 
    private final String FILE_KEY = "file";
    private final String STATUS_KEY = "status";
    private final String FILE_NAME_KEY = "fileName";
    String rootdir ="";
    String merchantCode ="";
    
    @EJB
    private OperatorMerchantValidationBean operatorMerchantValidationBean;
    @EJB
    private UploadFileConfigBean uploadFileConfigBean;

    private String generateFileName(String merchantCode, String outletId,
            String operatorName, String uploadDate) {
        return merchantCode + outletId + operatorName + uploadDate + ".txt";
    }

    @Override
    public void init() throws ServletException {
        File f = new File(System.getProperty("com.sun.aas.instanceRoot"));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        HashMap<String, String> result = new HashMap<String, String>();

        String applicationKey = request.getParameter(APP_KEY);
        String merchantCode = request.getParameter(MERCHANT_CODE_KEY);
        String userName = request.getParameter(USER_NAME_KEY);
        String sessionId = request.getParameter(SESSION_KEY);
        String token = request.getParameter(TOKEN_KEY);
        String outletId = request.getParameter(OUTLET_ID_KEY);
        String file = request.getParameter(FILE_KEY);
        String fileName = request.getParameter(FILE_NAME_KEY);
        
        File f = new File(System.getProperty("com.sun.aas.instanceRoot"));
        rootdir = f.getAbsolutePath() + File.separator + "docroot" + File.separator;
       // System.out.println("merchant code : " + merchantCode);
        pathOfConfigFile = rootdir + dirConfig + File.separator + merchantCode + File.separator;
        File mposConfigDir = new File(pathOfConfigFile);
        if (!mposConfigDir.exists()) {
            mposConfigDir.mkdirs();
        }

        PrintWriter writer = response.getWriter();

        if (applicationKey == null || merchantCode == null || userName == null
                || sessionId == null || token == null || outletId == null) {
            result.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
            writer.write(new JSONObject(result).toString());
            writer.close();
            return;
        }

        JSONObject responseValidate = operatorMerchantValidationBean.validateOperatorMPOS(applicationKey,
                merchantCode, userName, sessionId, token);

        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            Part part;
            try {
                part = request.getPart(FILE_KEY);

            } catch (IOException | ServletException e) {
                result.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
                writer.write(new JSONObject(result).toString());
                writer.close();
                return;
            }
            InputStream is = part.getInputStream();
            String outputFile = pathOfConfigFile + fileName;

            FileOutputStream os = new FileOutputStream(outputFile);
            int ch = is.read();
            while (ch != -1) {
                os.write(ch);
                ch = is.read();
            }

            JSONObject uploadConfigFileResponse = null;
            uploadConfigFileResponse = uploadFileConfigBean.insertFileConfig(merchantCode, userName,
                    outletId, fileName, outputFile);
            writer.write(uploadConfigFileResponse.toString());
            writer.close();
            os.close();

        } else {
            writer.write(responseValidate.toString());
            writer.close();
        }

    }
    
}

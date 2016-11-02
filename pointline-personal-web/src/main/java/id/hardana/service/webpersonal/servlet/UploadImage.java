/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal.servlet;

import id.hardana.ejb.webpersonal.registration.ImageUrlDBUpdaterBean;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.system.validation.PersonalValidationBean;
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
 * @author Trisna
 */
@WebServlet(name = "UploadImage", urlPatterns = {"/emo/uploadimage"})
@MultipartConfig
public class UploadImage extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public boolean retMd5 = false;
    private String filePathIdCard;
    private String filePathProfilePict;
    private String filePathSignature;
    private final String dirIdCardPersonal = "idcardpersonal";
    private final String dirProfilePict = "profilepictpersonal";
    private final String dirSignature = "signaturepersonal";
    private final String IDCARD_UPLOAD_KEY = "IDCARD";
    private final String PROFILE_PICT_UPLOAD_KEY = "PROFILEPICT";
    private final String SIGNATURE_UPLOAD_KEY = "SIGNATURE";
    private final String APP_KEY_KEY = "applicationKey";
    private final String ACCOUNT_KEY = "account";
    private final String SESSION_KEY = "sessionId";
    private final String TOKEN_KEY = "token";
    private final String FILE_KEY = "file";
    private final String STATUS_KEY = "status";
    private final String UPLOAD_TYPE_KEY = "type";
    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private ImageUrlDBUpdaterBean imageUrlDBUpdaterBean;

    private String generateFileName(String account, String token) {
        return account + token + ".jpg";
    }

    @Override

    public void init() throws ServletException {
        File f = new File(System.getProperty("com.sun.aas.instanceRoot"));
        String rootdir = f.getAbsolutePath() + File.separator + "docroot" + File.separator;
        filePathIdCard = rootdir + dirIdCardPersonal + File.separator;
        filePathProfilePict = rootdir + dirProfilePict + File.separator;
        filePathSignature = rootdir + dirSignature + File.separator;

        File idCardDir = new File(filePathIdCard);
        File profilePictDir = new File(filePathProfilePict);
        File signatureDir = new File(filePathSignature);
        if (!idCardDir.exists()) {
            idCardDir.mkdirs();
        }
        if (!profilePictDir.exists()) {
            profilePictDir.mkdirs();
        }
        if (!signatureDir.exists()) {
            signatureDir.mkdirs();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        HashMap<String, String> result = new HashMap<String, String>();

        String applicationKey = request.getParameter(APP_KEY_KEY);
        String account = request.getParameter(ACCOUNT_KEY);
        String sessionId = request.getParameter(SESSION_KEY);
        String token = request.getParameter(TOKEN_KEY);
        String uploadType = request.getParameter(UPLOAD_TYPE_KEY);

        PrintWriter writer = response.getWriter();

        if (applicationKey == null || account == null
                || sessionId == null || token == null || uploadType == null) {
            result.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
            writer.write(new JSONObject(result).toString());
            writer.close();
            return;
        }

        if (!uploadType.equals(IDCARD_UPLOAD_KEY) && !uploadType.equals(PROFILE_PICT_UPLOAD_KEY)
                && !uploadType.equals(SIGNATURE_UPLOAD_KEY)) {
            result.put(STATUS_KEY, ResponseStatusEnum.INVALID_UPLOAD_IMAGE_TYPE.getResponseStatus());
            writer.write(new JSONObject(result).toString());
            writer.close();
            return;
        }

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            result.put(STATUS_KEY, ResponseStatusEnum.INVALID_PHONE_NUMBER.getResponseStatus());
            writer.write(new JSONObject(result).toString());
            writer.close();
            return;
        }

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);

        if (responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {

            Part part;
            try {
                part = request.getPart(FILE_KEY);
                if (part.getSize() == 0) {
                    result.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
                    writer.write(new JSONObject(result).toString());
                    writer.close();
                    return;
                }
                if (part.getSize() > 1048576) {
                    result.put(STATUS_KEY, ResponseStatusEnum.IMAGE_SIZE_OVER_LIMIT.getResponseStatus());
                    writer.write(new JSONObject(result).toString());
                    writer.close();
                    return;
                }
            } catch (Exception e) {
                result.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
                writer.write(new JSONObject(result).toString());
                writer.close();
                return;
            }
            InputStream is = part.getInputStream();
            String outputfile = null;
            switch (uploadType) {
                case IDCARD_UPLOAD_KEY:
                    outputfile = filePathIdCard + generateFileName(formattedAccount, token);
                    break;
                case PROFILE_PICT_UPLOAD_KEY:
                    outputfile = filePathProfilePict + generateFileName(formattedAccount, token);
                    break;
                case SIGNATURE_UPLOAD_KEY:
                    outputfile = filePathSignature + generateFileName(formattedAccount, token);
                    break;
            }

            FileOutputStream os = new FileOutputStream(outputfile);
            int ch = is.read();
            while (ch != -1) {
                os.write(ch);
                ch = is.read();
            }

            JSONObject updateImageResponse = null;
            switch (uploadType) {
                case IDCARD_UPLOAD_KEY:
                    updateImageResponse = imageUrlDBUpdaterBean.idCardImageUpdate(formattedAccount, outputfile);
                    break;
                case PROFILE_PICT_UPLOAD_KEY:
                    updateImageResponse = imageUrlDBUpdaterBean.profilePictImageUpdate(formattedAccount, outputfile);
                    break;
                case SIGNATURE_UPLOAD_KEY:
                    updateImageResponse = imageUrlDBUpdaterBean.signatureImageUpdate(formattedAccount, outputfile);
                    break;
            }
            writer.write(updateImageResponse.toString());
            writer.close();
            os.close();

        } else {
            writer.write(responseValidate.toString());
            writer.close();
        }

    }
}

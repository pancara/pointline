/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.webpersonal.servlet;

import id.hardana.ejb.webpersonal.registration.ImageUrlDBViewerBean;
import id.hardana.ejb.system.tools.PhoneNumberVadalidator;
import id.hardana.ejb.system.validation.PersonalValidationBean;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * @author Trisna
 */
@WebServlet(name = "ViewImage", urlPatterns = {"/emo/viewimage"})
public class ViewImage extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String APP_KEY_KEY = "applicationKey";
    private final String ACCOUNT_KEY = "account";
    private final String SESSION_KEY = "sessionId";
    private final String TOKEN_KEY = "token";
    private final String STATUS_KEY = "status";
    private final String VIEW_TYPE_KEY = "type";
    private final String URL_KEY = "url";
    private final String IDCARD_VIEW_KEY = "IDCARD";
    private final String PROFILE_PICT_VIEW_KEY = "PROFILEPICT";
    private final String SIGNATURE_VIEW_KEY = "SIGNATURE";
    @EJB
    private PersonalValidationBean personalValidationBean;
    @EJB
    private ImageUrlDBViewerBean imageUrlDBViewerBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String applicationKey = request.getParameter(APP_KEY_KEY);
        String account = request.getParameter(ACCOUNT_KEY);
        String sessionId = request.getParameter(SESSION_KEY);
        String token = request.getParameter(TOKEN_KEY);
        String viewType = request.getParameter(VIEW_TYPE_KEY);

        if (applicationKey == null || account == null
                || sessionId == null || token == null || viewType == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!viewType.equals(IDCARD_VIEW_KEY) && !viewType.equals(PROFILE_PICT_VIEW_KEY)
                && !viewType.equals(SIGNATURE_VIEW_KEY)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String formattedAccount = PhoneNumberVadalidator.formatPhoneNumber(account);
        if (formattedAccount == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JSONObject responseValidate = personalValidationBean.validatePersonal(applicationKey,
                account, sessionId, token);
        if (!responseValidate.getString(STATUS_KEY).equals(ResponseStatusEnum.VALID.getResponseStatus())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JSONObject responseViewUrl = null;
        switch (viewType) {
            case IDCARD_VIEW_KEY:
                responseViewUrl = imageUrlDBViewerBean.idCardImageView(formattedAccount);
                break;
            case PROFILE_PICT_VIEW_KEY:
                responseViewUrl = imageUrlDBViewerBean.profilePictImageView(account);
                break;
            case SIGNATURE_VIEW_KEY:
                responseViewUrl = imageUrlDBViewerBean.signatureImageView(account);
                break;
        }
        if (!responseViewUrl.getString(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String filePath = responseViewUrl.getString(URL_KEY);
        File file = new File(filePath);

        response.setContentType(getServletContext().getMimeType(file.getAbsolutePath()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName());
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (FileNotFoundException fne) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}

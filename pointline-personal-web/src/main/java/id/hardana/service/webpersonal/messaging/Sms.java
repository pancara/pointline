package id.hardana.service.webpersonal.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Sms {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        URL url;

        try {
            String msisdn = URLEncoder.encode(args[0], "UTF-8");
            String message = args[1];

			//String msisdn = URLEncoder.encode("085722530233","UTF-8");
            //String message = URLEncoder.encode("!@#$%^&*()_:;~'","UTF-8");		
            String urlSource = "http://demo.demolinkit:demolinkit@sms.linkit360.com:8080/api/single/?from=SMSBLAST360&msisdn=" + msisdn + "&message=" + message;
            //System.out.println(urlSource);

            final String authUser = "demo.demolinkit";
            final String authPassword = "demolinkit";
            Authenticator.setDefault(
                    new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    authUser, authPassword.toCharArray());
                        }
                    }
            );

            System.setProperty("http.proxyUser", authUser);
            System.setProperty("http.proxyPassword", authPassword);

            url = new URL(urlSource);
            URLConnection conn = url.openConnection();
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                //System.out.println( inputLine);
            }
            br.close();

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}

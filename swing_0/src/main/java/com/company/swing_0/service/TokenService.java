/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.swing_0.service;

import com.company.swing_0.util.NotisUtil;
import com.company.swing_0.util.PropertiesLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author Aghil
 */
public class TokenService {

    private static final String GRANT_TYPE_URL_DATA = "grant_type=client_credentials";
    private static final String TOKEN_REQUEST_CONTENT_TYPE = "application/x-www-form-urlencoded";

    public String getToken() throws Exception {
        String authorization = NotisUtil.getAuthorization();
        String nonce = NotisUtil.getNonce();
        String tokenRequestHost = PropertiesLoader.getProperty("token.host");
        String tokenRequestEndpoint = PropertiesLoader.getProperty("token.endpoint");
        String urlString = "https://" + tokenRequestHost + tokenRequestEndpoint;

        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", TOKEN_REQUEST_CONTENT_TYPE);
        connection.setRequestProperty("Authorization", authorization);
        connection.setRequestProperty("nonce", nonce);

        byte[] postData = GRANT_TYPE_URL_DATA.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData);
            os.flush();
            os.close();
        }
        int responseCode = connection.getResponseCode();
        return this.postTokenRequest(connection);
    }

    private String postTokenRequest(HttpsURLConnection connection) {
        String input;
        String accessToken = "";
        if (connection != null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((input = br.readLine()) != null) {
                    System.out.println(input);
                }
                br.close();

                // https://www.danvega.dev/blog/2017/07/05/read-json-data-spring-boot-write-database/
                // https://attacomsian.com/blog/jackson-convert-json-string-to-json-node
                // sample response data => { "access_token":
                // "ee1073de-45d0-4040-b9c2-eddfa80280c0", "token_type": "bearer", "expires_in":
                // "3600", "scope": "api_scope" }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(input);
                accessToken = node.get("access_token").asText();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return accessToken;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.swing_0.service;

import com.company.swing_0.util.NotisUtil;
import com.company.swing_0.util.PropertiesLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Aghil
 */
public class TradeInquiryService {

    private static final String DATA_FORMAT = "CSV:CSV";
    private static final String VERSION = "1.0";
    private static final String ALL_DATA_TRADE_INQUIRY = "0,ALL,,";

    public String getAllTradeData() throws Exception {
        String authorization = NotisUtil.getAuthorization();
        String nonce = NotisUtil.getNonce();
        String postData = this.getAllTradeRequestData();

        String tradeInquirytHost = PropertiesLoader.getProperty("tradeinquiry.host");
        String tradeInquiryEndpoint = PropertiesLoader.getProperty("tradeinquiry.endpoint");
        String urlString = "https://" + tradeInquirytHost + tradeInquiryEndpoint;

        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", authorization);
        connection.setRequestProperty("nonce", nonce);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes("utf-8");
            os.write(input, 0, input.length);
            os.flush();
            os.close();
        }

        String responseLine = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder respone = new StringBuilder();
            responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                respone.append(responseLine.trim());
            }
        }
        return responseLine;
    }

    private String getAllTradeRequestData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("*****");
        ObjectNode data1 = mapper.createObjectNode();
        data1.put("msgId", NotisUtil.getMsgId());
        data1.put("dataFormat", DATA_FORMAT);
        data1.put("tradesInquiry", ALL_DATA_TRADE_INQUIRY);
        ObjectNode jsonData = mapper.createObjectNode();
        jsonData.put("version", VERSION);
        jsonData.put("data", data1.toString());
        return jsonData.asText();
    }
}

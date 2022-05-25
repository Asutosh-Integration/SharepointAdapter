/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asu.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The www.Sample.com producer.
 */
public class SharepointComponentProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(SharepointComponentProducer.class);
    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String GET_URL = "";

    private static final String POST_URL = "";
    private static final String CT = "";


    private SharepointComponentEndpoint endpoint;

	public SharepointComponentProducer(SharepointComponentEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    public void process(final Exchange exchange) throws Exception {
        
        String input = exchange.getIn().getBody(String.class);
        String header = (String) exchange.getIn().getHeader("CT");
        String Auth = (String) exchange.getIn().getHeader("Auth");
        String Auth1 = Auth;
        String URL = endpoint.getURL();
        String TenantID = endpoint.getTenantID();
        String Resource = endpoint.getResource();
        String Credential = endpoint.getCredential();
        String FilePath = endpoint.getFilePath();
        String GET_URL;
        GET_URL = URL;
        GET_URL = GET_URL + "_api/web/GetFolderByServerRelativePath(DecodedUrl=%27%2Fsites%2FAsutoshIntegration%2FShared%20Documents%2FGeneral%27)/Files/AddUsingPath(DecodedUrl=%27test2.txt%27,AutoCheckoutOnInvalidData=true)";

        //Oauth Call
        String OauthUrl = "https://accounts.accesscontrol.windows.net/" + TenantID + "/tokens/OAuth/2";
        String OauthBody = "grant_type=client_credentials&client_id=6771d88f-6141-401c-8ec4-a7e278d9e15e@36da45f1-dd2c-4d1f-af13-5abe46b99921&client_secret=75FZTXLGbXzhIPzQGsRG/dOeILEPmtzDnSCc4mxdLoU=&resource=00000003-0000-0ff1-ce00-000000000000/amedeloitte.sharepoint.com@36da45f1-dd2c-4d1f-af13-5abe46b99921";
        String OauthContentType = "application/x-www-form-urlencoded";
        String OauthAuthorisation = null;
        String OauthResult = sendPOST(OauthUrl, OauthBody, OauthContentType, OauthAuthorisation);
        ObjectMapper mapper = new ObjectMapper();
        String json = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
        JsonNode jsonNode = mapper.readTree(json);
        String color = jsonNode.get("color").asText();

		if(URL == null || URL.isEmpty()) {
			URL = "(Producer) Hello!";
		}
		String messageInUpperCase = URL.toUpperCase();
		if (input != null) {
		    LOG.debug(input);
			messageInUpperCase = input + " (Producer) : " + messageInUpperCase  + TenantID + Credential + Resource + FilePath + OauthResult + color;
		}
		exchange.getIn().setBody(messageInUpperCase);
        System.out.println(messageInUpperCase);

    }

    private static String sendGET(String GET_URL) throws IOException {
        URL obj = new URL(GET_URL);
        String output = null;
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            output = response.toString();
        } else {
            System.out.println("GET request not worked");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            output = response.toString();

        }
        return output;
    }

    private static String sendPOST(String POST_URL, String POST_BODY, String CT, String Auth) throws IOException {
        URL obj = new URL(POST_URL);
        String output = null;
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-type", CT);
        if (Auth != null) {
            con.setRequestProperty("Authorization", Auth);
        }
        String jsonInputString = POST_BODY;
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();

        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            output = response.toString();
        } else {
            System.out.println("POST request not worked");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            output = response.toString();
        }
        return output;
    }

}

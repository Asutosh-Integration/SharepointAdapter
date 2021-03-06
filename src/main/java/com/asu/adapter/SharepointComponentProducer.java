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
import org.apache.camel.WrappedFile;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.language.simple.SimpleLanguage;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.sap.it.api.securestore.SecureStoreService;
import com.sap.it.api.securestore.UserCredential;
import com.sap.it.api.ITApiFactory;

/**
 * The www.Sample.com producer.
 */
public class SharepointComponentProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(SharepointComponentProducer.class);
    private static final String GET_URL = "";
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
        
        Object input = exchange.getIn().getBody();
        if (input instanceof WrappedFile) {
            // unwrap file
            input = ((WrappedFile) input).getFile();
        }

        InputStream is;
        if (input instanceof InputStream) {
            is = (InputStream) input;
        } else if (input instanceof File) {
            is = new FileInputStream((File)input);
        } else if (input instanceof byte[]) {
            is = new ByteArrayInputStream((byte[]) input);
        } else {
            // try as input stream
            is = exchange.getContext().getTypeConverter().tryConvertTo(InputStream.class, exchange, input);
        }
        String Domain = getValue(endpoint.getDomain(),exchange);
        String TenantID = getValue(endpoint.getTenantID(),exchange);
        String Resource = getValue(endpoint.getResource(),exchange);
        String Credential = getValue(endpoint.getCredential(),exchange);
        String FolderPath = encodeValue(getValue(endpoint.getFolderPath(),exchange));
        String FileName = encodeValue(getValue(endpoint.getFileName(),exchange));
        String Site = getValue(endpoint.getSite(),exchange);
        String GET_URL;
        GET_URL = "https://" + Domain + "/sites/" + Site + "/_api/web/GetFolderByServerRelativePath(DecodedUrl=@a1)/Files/AddUsingPath(DecodedUrl=@a2,AutoCheckoutOnInvalidData=@a3)?@a1=%27%2Fsites%2F" + Site + "%2FShared%20Documents%2F" + FolderPath + "%27&@a2=%27" + FileName + "%27&@a3=true&$Select=ServerRelativeUrl,UniqueId,Name,VroomItemID,VroomDriveID,ServerRedirectedUrl&$Expand=ListItemAllFields";
        SecureStoreService secureStoreService = ITApiFactory.getService(SecureStoreService.class, null);
        UserCredential userCredential = secureStoreService.getUserCredential(Credential);
        char[] ch = userCredential.getPassword();
        String ClientSecret = new String(ch);
        String ClientID = userCredential.getUsername();
        //Oauth Call
        String OauthUrl = "https://accounts.accesscontrol.windows.net/" + TenantID + "/tokens/OAuth/2";
        String result = getOauthTokenUsingClientCredential(OauthUrl,ClientID,TenantID,ClientSecret,Resource,Domain);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result);
        String Token = jsonNode.get("access_token").asText();
        //Final Call
        String finalResult = sendFiletoSharepoint(GET_URL,Token,is);

		if(Domain == null || Domain.isEmpty()) {
			Domain = "(Producer) Hello!";
		}
		String messageInUpperCase = Domain.toUpperCase();
		if (input != null) {

			messageInUpperCase = finalResult;
		}
		exchange.getIn().setBody(messageInUpperCase);
        System.out.println(messageInUpperCase);



    }

    private static String getOauthTokenUsingClientCredential(String url, String clientID, String tenantID, String clientSecret, String resource, String domain) throws IOException {
        String result = "";
        HttpPost post = new HttpPost(url);
        String body = "grant_type=client_credentials&client_id=" + clientID + "@" + tenantID + "&client_secret=" + clientSecret + "&resource=" + resource + "/" + domain + "@" + tenantID;
        StringEntity b = new StringEntity(body);
        post.setEntity(b);
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            result = EntityUtils.toString(response.getEntity());
        }
        return result;
    }

    private static String sendFiletoSharepoint(String url, String Token, InputStream body) throws IOException {
        String result = "";
        HttpPost post = new HttpPost(url);
        post.addHeader("Authorization","Bearer "+Token);
        byte[] bytes = IOUtils.toByteArray(body);
        ByteArrayEntity entity = new ByteArrayEntity(bytes,ContentType.APPLICATION_OCTET_STREAM);
        post.setEntity(entity);
        try (CloseableHttpClient httpClient = HttpClients.custom().build();
             CloseableHttpResponse response = httpClient.execute(post)){
            result = EntityUtils.toString(response.getEntity());
        }
        return result;
    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    public String getValue(String paramName, Exchange exchange) throws IllegalArgumentException {
        String answer = null;
        if ((paramName.startsWith("$simple{") || paramName.startsWith("${")) && paramName.endsWith("}")) {
            paramName = paramName.replace('#', ':');

            answer = (String) SimpleLanguage.expression(paramName).evaluate(exchange, Object.class);
        }else {
            answer = paramName;
        }
        return answer;
    }
}




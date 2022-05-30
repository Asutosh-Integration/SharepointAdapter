package com.asu.adapter;


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

import java.io.File;
import java.net.URISyntaxException;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultPollingEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a www.Sample.com Camel endpoint.
 */
@UriEndpoint(scheme = "sap-sample", syntax = "", title = "")
public class SharepointComponentEndpoint extends DefaultPollingEndpoint {
    private SharepointComponentComponent component;

    private transient Logger logger = LoggerFactory.getLogger(SharepointComponentEndpoint.class);

    @UriParam
    private String greetingsMessage;
    private  String Domain;
    private  String TenantID;
    private  String Resource;
    private  String FolderPath;
    private  String Credential;
    private String Site;
    private String FileName;
    
    @UriParam
    private boolean useFormater;

	public boolean getUseFormater() {
		return useFormater;
	}

	public void setUseFormater(boolean useFormater) {
		this.useFormater = useFormater;
	}

	public String getGreetingsMessage() {
		return greetingsMessage;
	}
    public String getDomain() {
        return Domain;
    }
    public String getTenantID() { return TenantID; }
    public  String getResource() { return Resource; }
    public String getFolderPath() { return FolderPath; }
    public String getCredential() { return Credential; }
    public String getSite() { return Site; }
    public String getFileName() { return FileName; }


	public void setGreetingsMessage(String greetingsMessage) {
		this.greetingsMessage = greetingsMessage;
	}
    public void setDomain(String Domain) {
        this.Domain = Domain;
    }
    public void setTenantID(String TenantID) { this.TenantID = TenantID; }
    public void setResource(String Resource) { this.Resource = Resource; }
    public void setFolderPath(String FolderPath) { this.FolderPath = FolderPath; }
    public void setCredential(String Credential) { this.Credential = Credential; }
    public void setSite(String Site) { this.Site = Site; }
    public void setFileName(String FileName) { this.FileName = FileName; }
	public SharepointComponentEndpoint() {
    }

    public SharepointComponentEndpoint(final String endpointUri, final SharepointComponentComponent component) throws URISyntaxException {
        super(endpointUri, component);
        this.component = component;
    }

    public SharepointComponentEndpoint(final String uri, final String remaining, final SharepointComponentComponent component) throws URISyntaxException {
        this(uri, component);
    }

    public Producer createProducer() throws Exception {
        return new SharepointComponentProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        final SharepointComponentConsumer consumer = new SharepointComponentConsumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }

    public boolean isSingleton() {
        return true;
    }
}

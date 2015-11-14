/*
 *
 *  *
 *  *  * ****************************************************************************
 *  *  * Copyright (c) 2015. Muriel Kamgang Mabou
 *  *  * All rights reserved.
 *  *  *
 *  *  * This file is part of project AndroidWPTemplate.
 *  *  * It can not be copied and/or distributed without the
 *  *  * express permission of Muriel Kamgang Mabou
 *  *  * ****************************************************************************
 *  *
 *  *
 *
 */

package hr.mk.wpmagazine.client;

import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Mur0 on 03/12/14.
 */
public class CookieRestTemplate extends RestTemplate {

    private final BasicCookieStore cookieStore;
    private final BasicHttpContext httpContext;
    private final DefaultHttpClient httpClient;

    public CookieRestTemplate() {
        httpContext = new BasicHttpContext();
        cookieStore = new BasicCookieStore();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        HttpParams params = new BasicHttpParams();
        HttpClientParams.setRedirecting(params, false);

        //REGISTERS SCHEMES FOR BOTH HTTP AND HTTPS
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        registry.register(new Scheme("https", sslSocketFactory, 443));

        ClientConnectionManager conman = new ThreadSafeClientConnManager(params, registry);
        httpClient = new DefaultHttpClient(conman, params);
        HttpComponentsClientHttpRequestFactory hcchrf = new StatefulHttpComponentsClientHttpRequestFactory(httpClient, httpContext);
        hcchrf.setConnectTimeout(30 * 1000);
        setRequestFactory(hcchrf);
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public BasicHttpContext getHttpContext() {
        return httpContext;
    }

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }
}

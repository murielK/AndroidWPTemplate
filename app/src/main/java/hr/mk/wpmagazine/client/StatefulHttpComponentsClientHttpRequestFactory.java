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

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

/**
 * Created by Mur0 on 03/12/14.
 */
public class StatefulHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
    private final HttpContext httpContext;

    public StatefulHttpComponentsClientHttpRequestFactory(HttpClient httpClient, HttpContext httpContext) {
        super(httpClient);
        this.httpContext = httpContext;
    }

    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return this.httpContext;
    }
}

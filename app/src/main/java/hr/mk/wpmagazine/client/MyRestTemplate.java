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

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import hr.mk.wpmagazine.Utils.TagFactoryUtils;

/**
 * Created by Mur0 on 02/06/2014.
 */
public class MyRestTemplate extends CookieRestTemplate {

    private static final String TAG = TagFactoryUtils.getTag(MyRestTemplate.class);
    private static volatile MyRestTemplate singleton;
    private String baseUrl;

    public MyRestTemplate() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        mapper.setDateFormat(dateFormat);

        MappingJackson2HttpMessageConverter mc = new MappingJackson2HttpMessageConverter();
        mc.setObjectMapper(mapper);
        getMessageConverters().add(mc);
        setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new ApiV2RequestInterceptor()));
    }

    public static MyRestTemplate getInstance(String baseUrl) {
        if (singleton == null) {
            synchronized (MyRestTemplate.class) {
                if (singleton == null) {
                    singleton = new MyRestTemplate();
                    singleton.setBaseUrl(baseUrl);
                }
            }
        }

        return singleton;
    }


    public String getBase() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Object... urlVariables) {
        Log.d(TAG, "request url:" + getBase() + url + " method: " + method.name());
        return super.execute(getBase() + url, method, requestCallback, responseExtractor, urlVariables);
    }

    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor,
                         Map<String, ?> urlVariables) {
        return super.execute(getBase() + url, method, requestCallback, responseExtractor, urlVariables);
    }

    @Override
    public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
        throw new UnsupportedOperationException();
    }


    public class ApiV2RequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution e) throws IOException {
            final HttpHeaders headers = httpRequest.getHeaders();
            headers.add("User-Agent", "WPAndroidTemplate");
            ClientHttpResponse response = e.execute(httpRequest, bytes);

            return response;
        }
    }
}
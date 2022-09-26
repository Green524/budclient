package com.chenum.config;

import org.apache.http.HttpResponse;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;

public class CustomServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {

    @Override
    public boolean retryRequest(HttpResponse httpResponse, int i, HttpContext httpContext) {
        System.out.println("retryRequest");
        System.out.println(httpResponse.getStatusLine().getStatusCode());
        return false;
    }

    @Override
    public long getRetryInterval() {
        System.out.println("getRetryInterval");
        return 0;
    }
}

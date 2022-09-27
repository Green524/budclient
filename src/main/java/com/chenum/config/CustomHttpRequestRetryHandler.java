package com.chenum.config;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.ConnectException;

public class CustomHttpRequestRetryHandler implements HttpRequestRetryHandler {
    @Override
    public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
        if (e instanceof ConnectException && i < 5){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("重试");
            System.out.println(i);

            return true;
        }
        return false;
    }
}

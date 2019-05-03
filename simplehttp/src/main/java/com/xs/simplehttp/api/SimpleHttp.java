package com.xs.simplehttp.api;



import com.xs.simplehttp.core.HttpCall;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * simple http
 *
 * Created by xs code on 2019/3/11.
 */

public class SimpleHttp {

    /**
     * connection time out
     */
    private static final int CONNETION_TIME_OUT = 60*1000;
    /**
     * read time out
     */
    private static final int READ_TIME_OUT = 60*1000;
    /**
     * base url
     */
    private String baseUrl;
    /**
     * SSLSocketFactory
     */
    private SSLSocketFactory sslSocketFactory;
    /**
     * HostnameVerifier
     */
    private HostnameVerifier hostnameVerifier;
    /**
     * connection time out
     */
    private int connectTimeOut = CONNETION_TIME_OUT;
    /**
     * read time out
     */
    private int readTimeOut = READ_TIME_OUT;
    /**
     * cookieJar
     */
    private CookieJar cookieJar;
    /**
     * interceptor list
     */
    private List<Interceptor> interceptors;


    public SimpleHttp(String baseUrl, SSLSocketFactory sslSocketFactory,
                      HostnameVerifier hostnameVerifier, int connectTimeOut,
                      int readTimeOut, CookieJar cookieJar, List<Interceptor> interceptors) {
        this.baseUrl = baseUrl;
        this.sslSocketFactory = sslSocketFactory;
        this.hostnameVerifier = hostnameVerifier;
        this.connectTimeOut = connectTimeOut;
        this.readTimeOut = readTimeOut;
        this.cookieJar = cookieJar;
        this.interceptors = interceptors;
    }

    public CookieJar getCookieJar() {
        return cookieJar;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public static class Builder {

        private String baseUrl;

        private SSLSocketFactory sslSocketFactory;

        private HostnameVerifier hostnameVerifier;

        private int connectTimeOut;

        private int readTimeOut;

        private CookieJar cookieJar;

        private List<Interceptor> interceptors = new ArrayList<>();

        public static Builder newBuild() {
            return new Builder();
        }

        /**
         * base url
         * @param baseUrl
         * @return
         */
        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * SslSocketFactory
         * @param sslSocketFactory
         * @return
         */
        public Builder setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        /**
         * HostnameVerifier
         * @param hostnameVerifier
         * @return
         */
        public Builder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        /**
         * set connect time out
         * @param connectTimeOut
         * @return
         */
        public Builder setConnectTimeOut(int connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
            return this;
        }

        /**
         * set read time out
         * @param readTimeOut
         * @return
         */
        public Builder setReadTimeOut(int readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        /**
         * set cookiejar
         * @param cookieJar
         * @return
         */
        public Builder setCookieJar(CookieJar cookieJar) {
            this.cookieJar = cookieJar;
            return this;
        }

        /**
         * add interceptor
         * @param interceptor
         * @return
         */
        public Builder addInterceptor(Interceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        public SimpleHttp build() {
            return new SimpleHttp(this.baseUrl,
                    this.sslSocketFactory,
                    this.hostnameVerifier,
                    this.connectTimeOut,
                    this.readTimeOut,
                    this.cookieJar,
                    this.interceptors);
        }
    }

    public Call newCall(Request request) {
        return new HttpCall(this,request);
    }


}

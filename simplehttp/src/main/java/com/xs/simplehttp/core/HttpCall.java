package com.xs.simplehttp.core;



import com.xs.simplehttp.api.Call;
import com.xs.simplehttp.api.Callback;
import com.xs.simplehttp.api.Interceptor;
import com.xs.simplehttp.api.Request;
import com.xs.simplehttp.api.Response;
import com.xs.simplehttp.api.SimpleHttp;
import com.xs.simplehttp.interceptot.CookieInterceptot;
import com.xs.simplehttp.interceptot.HttpInterceptor;
import com.xs.simplehttp.util.ThreadChange;

import java.util.ArrayList;
import java.util.List;

/**
 * http call
 *
 * Created by xs code on 2019/3/12.
 */

public class HttpCall implements Call {

    private SimpleHttp simpleHttp;
    private Request request;
    private boolean executed;
    private Callback callback;
    private HttpInterceptor httpInterceptor;


    public HttpCall(SimpleHttp simpleHttp, Request request) {
        this.simpleHttp = simpleHttp;
        this.request = request;
        this.httpInterceptor = new HttpInterceptor(simpleHttp);
    }

    @Override
    public Response execute() {
        if (executed) {
            throw new IllegalAccessError("call is already executed");
        }
        executed = true;
        return getResponse();
    }

    @Override
    public void enqueue(Callback callback) {
        this.callback = callback;
        if (executed) {
            throw new IllegalAccessError("call is already executed");
        }
        executed = true;
        ThreadChange.INSTANCE.execute(ThreadChange.POOL, new Runnable() {
            @Override
            public void run() {
                final Response response = getResponse();
                if (response.isSuccessful()) {
                    ThreadChange.INSTANCE.execute(ThreadChange.MAIN, new Runnable() {
                        @Override
                        public void run() {
                            onResponse(response);
                        }
                    });
                } else {
                    ThreadChange.INSTANCE.execute(ThreadChange.MAIN, new Runnable() {
                        @Override
                        public void run() {
                            onFailure(response.getError());
                        }
                    });

                }

            }
        });

    }

    private void onResponse(Response response){
        if (callback != null) {
            callback.onResponse(response);
        }
    }

    private void onFailure(HttpException e){
        if (callback != null) {
            callback.onFailure(e);
        }
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        callback = null;
        httpInterceptor.Cancel();
    }

    @Override
    public boolean isCanceled() {
        return httpInterceptor.isCanceled();
    }

    @Override
    public Request request() {
        return request;
    }

    private Response getResponse() {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(simpleHttp.getInterceptors());
        interceptors.add(new CookieInterceptot(simpleHttp.getCookieJar(),simpleHttp.getBaseUrl()));
        interceptors.add(httpInterceptor);
        Interceptor.Chain chain = new RealChain(interceptors,request,0);
        return chain.proceed(request);
    }
}

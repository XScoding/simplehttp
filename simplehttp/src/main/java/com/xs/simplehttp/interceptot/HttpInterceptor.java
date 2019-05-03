package com.xs.simplehttp.interceptot;


import com.xs.simplehttp.api.Interceptor;
import com.xs.simplehttp.api.Request;
import com.xs.simplehttp.api.Response;
import com.xs.simplehttp.api.SimpleHttp;
import com.xs.simplehttp.core.Http;

/**
 * http interceptor
 *
 * Created by xs code on 2019/3/14.
 */

public class HttpInterceptor implements Interceptor {

    private boolean cancel;
    private SimpleHttp simpleHttp;
    private Http http;

    public HttpInterceptor(SimpleHttp simpleHttp) {
        this.simpleHttp = simpleHttp;
    }

    @Override
    public Response intercept(Chain chain){
        Request request = chain.request();
        http = Http.create(simpleHttp);
        Response response = http.getResponse(request);
        return response;
    }

    public void Cancel() {
        if (!cancel && http != null) {
            http.Cancel();
        }
        cancel = true;
    }

    public boolean isCanceled() {
        return cancel;
    }
}

package com.xs.simplehttp.core;


import com.xs.simplehttp.api.Interceptor;
import com.xs.simplehttp.api.Request;
import com.xs.simplehttp.api.Response;

import java.util.List;

/**
 * interceptor execute
 *
 * Created by xs code on 2019/3/14.
 */

public class RealChain implements Interceptor.Chain {
    private List<Interceptor> interceptors;
    private Request request;
    private int index;

    public RealChain(List<Interceptor> interceptors, Request request,int index) {
        this.interceptors = interceptors;
        this.request = request;
        this.index = index;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response proceed(Request request) {
        if (index >= interceptors.size()) {
            throw new IllegalAccessError("interceptor is not over");
        }

        Interceptor.Chain chain = new RealChain(interceptors,request,index+1);
        Interceptor interceptor = interceptors.get(index);
        Response response = interceptor.intercept(chain);

        if (response == null) {
            throw new NullPointerException("response" + interceptor + "is null");
        }

        return response;
    }
}

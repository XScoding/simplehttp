package com.xs.simplehttp.interceptot;



import com.xs.simplehttp.api.CookieJar;
import com.xs.simplehttp.api.Interceptor;
import com.xs.simplehttp.api.Request;
import com.xs.simplehttp.api.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cookie interceptor
 *
 * Created by xs code on 2019/3/14.
 */

public class CookieInterceptot implements Interceptor {
    
    
    private CookieJar cookieJar;
    
    private String url;

    public CookieInterceptot(CookieJar cookieJar, String url) {
        this.cookieJar = cookieJar;
        this.url = url;
    }

    @Override
    public Response intercept(Chain chain) {
        Request request = chain.request();
        if (cookieJar != null) {
            Map<String, String> cookies = cookieJar.load(url);
            if(cookies != null && cookies.size() > 0) {
                StringBuffer sb = new StringBuffer();
                boolean first = true;
                for (Map.Entry<String, String> entry : cookies.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(";");
                    }
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                }
                String cookie = sb.toString();
                request.getHeader().put("Cookie", cookie);
            }
        }


        Response response = chain.proceed(request);

        if (cookieJar != null &&
                response.getHeaderFields() != null &&
                response.getHeaderFields().size() > 0) {
            List<String> list = response.getHeaderFields().get("Set-Cookie");
            if (list != null && list.size() > 0) {
                Map<String,String> map = new HashMap<>();
                for (String cookie : list) {
                    int i = cookie.indexOf(";");
                    if (i > 0) {
                        String substring = cookie.substring(0, i);
                        String[] split = substring.split("=");
                        map.put(split[0],split[1]);
                    }
                }
                cookieJar.save(url,map);
            }
        }
        return response;
    }
}

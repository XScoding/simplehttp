package com.xs.simplehttp.api;

import java.util.Map;

/**
 * handle cookie
 *
 * Created by xs code on 2019/3/13.
 */

public interface CookieJar {

    /**
     * cookies save
     * @param url
     * @param cookies
     */
    void save(String url, Map<String, String> cookies);

    /**
     * cookies load
     * @param url
     * @return
     */
    Map<String,String> load(String url);
}

package com.xs.simplehttp.core;

import android.text.TextUtils;


import com.xs.simplehttp.api.Multiparts;
import com.xs.simplehttp.api.ProgressListener;
import com.xs.simplehttp.api.Request;
import com.xs.simplehttp.api.Response;
import com.xs.simplehttp.api.SimpleHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * httpUrlConnection connect
 *
 * Created by xs code on 2019/3/11.
 */

public class Http {

    private static final String BOUNDARY = java.util.UUID.randomUUID().toString();
    private static final String TWO_HYPHENS = "--";
    private static final String TWO_HYPHENS_BOUNDARY = "--Boundary-";
    private static final String LINE_END = "\r\n";
    private static final int COPY_LENTH = 1024 * 10;

    /**
     * HttpURLConnection
     */
    HttpURLConnection httpURLConnection;

    /**
     * SimpleHttp
     */
    private SimpleHttp simpleHttp;

    /**
     * cancel flag
     */
    private boolean cancel;

    /**
     * create Http
     * @param simpleHttp
     * @return
     */
    public static Http create(SimpleHttp simpleHttp) {
        Http http = new Http();
        http.simpleHttp = simpleHttp;
        return http;
    }

    /**
     * http get Response
     * @param request
     * @return
     */
    public Response getResponse(Request request) {
        switch (request.getMethodType()) {
            case GET:
                return getData(request);
            case POST:
                return postData(request);
            case UPLOAD:
                return uploadFile(request);
        }
        return null;
    }

    /**
     * cancel connect
     */
    public void Cancel() {
        if (!cancel && httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        cancel = true;
    }

    /**
     * cancel state
     * @return
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * Get connection
     */
    private Response getData(Request request){
        HttpURLConnection conn = null;
        try {
            conn= getHttpURLConnection(getUrl(append(request.getPath()),request.getParams()),"GET");
            conn.setDoInput(true);
            setHeader(conn,request.getHeader());
            if (cancel) {
                return Response.create(conn,new HttpException("connecton is canceled",0),request);
            } else {
                conn.connect();
                httpURLConnection = conn;
            }
            return Response.create(conn,request);
        } catch (Exception e) {
            return Response.create(conn,e,request);
        }
    }

    /**
     * Post connection
     */
    private Response postData(Request request) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(append(request.getPath()),"POST");
            conn.setDoOutput(true);//可写出
            conn.setDoInput(true);//可读入
            conn.setUseCaches(false);//不是有缓存
            if(request.getPostType() != null) {
                conn.setRequestProperty("Content-Type", request.getPostType().getType());
            }
            setHeader(conn,request.getHeader());//请求头必须放在conn.connect()之前
            // 连接，以上所有的请求配置必须在这个API调用之前
            if (cancel) {
                return Response.create(conn,new HttpException("connecton is canceled",0),request);
            } else {
                conn.connect();
                httpURLConnection = conn;
            }
            if((request.getParams() != null && request.getParams().size() > 0 ) ||
                    !TextUtils.isEmpty(request.getJsonParams())) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.write(getPostBody(request.getPostType(),request.getParams(),request.getJsonParams()));
                writer.close();
            }
            return Response.create(conn,request);
        } catch (Exception e) {
            return Response.create(conn,e,request);
        }
    }


    /**
     * Upload connection
     */
    private Response uploadFile(Request request) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(append(request.getPath()),"POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=Boundary-" + BOUNDARY);
            setHeader(conn,request.getHeader());
            if (cancel) {
                return Response.create(conn,new HttpException("connecton is canceled",0),request);
            } else {
                conn.connect();
                httpURLConnection = conn;
            }
            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            if (request.getParams() != null && request.getParams().size() > 0) {
                outputStream.write(getParamsBytesString(request.getParams()).getBytes());//上传参数
                outputStream.flush();
            }
            if (request.getMultipart() != null && request.getMultipart().getParts().size() > 0) {
                for (Multiparts.Part part : request.getMultipart().getParts()) {
                    if (part.getFile() != null) {
                        writeFile(part.getFile(),part.getFileKey(),part.getFileType(),part.getFileName(),outputStream,request.getUploadListener());
                    } else if (part.getBytes() != null) {
                        writeByte(part.getBytes(),part.getFileKey(),part.getFileType(),part.getFileName(),outputStream,request.getUploadListener());
                    }
                }
            }
            byte[] endData = (LINE_END + TWO_HYPHENS_BOUNDARY + BOUNDARY + TWO_HYPHENS + LINE_END).getBytes();//写结束标记位
            outputStream.write(endData);
            outputStream.flush();
            return Response.create(conn,request);
        } catch (Exception e) {
            return Response.create(conn,e,request);
        }
    }

    /**
     * upload file write file
     */
    private void writeFile(File file, String fileKey, String fileType,String fileName, DataOutputStream outputStream,ProgressListener listener) throws IOException {
        outputStream.write(getFileParamsString(file, fileKey, fileType,fileName).getBytes());
        outputStream.flush();

        FileInputStream inputStream = new FileInputStream(file);
        final long total = file.length();
        long sum = 0;
        byte[] buffer = new byte[COPY_LENTH];
        int length = -1;
        while ((length = inputStream.read(buffer)) != -1){
            outputStream.write(buffer,0,length);
            sum = sum + length;
            if(listener != null){
                final long finalSum = sum;
                listener.onProgress(finalSum * 100.0f / total,total);
            }
        }
        outputStream.flush();
        inputStream.close();
    }

    /**
     * upload file write bytes
     */
    private void writeByte(byte[] bytes, String fileKey, String fileType, String fileName, DataOutputStream outputStream, ProgressListener listener) throws IOException {
        outputStream.write(getFileParamsString(bytes,fileKey,fileType, fileName).getBytes());
        outputStream.flush();
        int total = bytes.length;
        int count = total/COPY_LENTH;
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (i == count - 1) {
                    byte[] range = Arrays.copyOfRange(bytes, i * COPY_LENTH, total);
                    outputStream.write(range,0, range.length);
                    if(listener != null){
                        listener.onProgress(total * 100.0f / total,total);
                    }
                } else {
                    byte[] range = Arrays.copyOfRange(bytes, i * COPY_LENTH, (i+1) * COPY_LENTH);
                    outputStream.write(range,0, range.length);
                    if(listener != null){
                        listener.onProgress((i + 1) * COPY_LENTH * 100.0f / total,total);
                    }
                }
            }
        } else {
            outputStream.write(bytes,0, total);
            if(listener != null){
                listener.onProgress(total * 100.0f / total,total);
            }
        }
        outputStream.flush();
    }

    /**
     * connection setting
     */
    private HttpURLConnection getHttpURLConnection(String urlString,String requestMethod) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (urlString.startsWith("https")) {
            HttpsURLConnection connection = (HttpsURLConnection) conn;
            connection.setConnectTimeout(simpleHttp.getConnectTimeOut());
            connection.setReadTimeout(simpleHttp.getReadTimeOut());
            connection.setRequestMethod(requestMethod);
            if (simpleHttp.getHostnameVerifier() != null) {
                connection.setHostnameVerifier(simpleHttp.getHostnameVerifier());
            }
            if (simpleHttp.getSslSocketFactory() != null) {
                connection.setSSLSocketFactory(simpleHttp.getSslSocketFactory());
            }
            return connection;
        } else {
            conn.setConnectTimeout(simpleHttp.getConnectTimeOut());
            conn.setReadTimeout(simpleHttp.getReadTimeOut());
            conn.setRequestMethod(requestMethod);
            return conn;
        }
    }

    /**
     * url append path
     * @param path
     * @return
     */
    private String append(String path) {
        return simpleHttp.getBaseUrl() + path;
    }

    /**
     * Get url append params
     */
    private String getUrl(String url,Map<String, Object> paramsMap) {
        StringBuffer sb = new StringBuffer(url);
        if(paramsMap != null && paramsMap.size() > 0){
            boolean first = true;
            for (String key: paramsMap.keySet()){
                if (first) {
                    sb.append("?");
                    first = false;
                } else {
                    sb.append("&");
                }
                sb.append(key).append("=").append(paramsMap.get(key));
            }
        }
        return sb.toString();
    }


    /**
     * set header
     */
    private void setHeader(HttpURLConnection conn, Map<String, Object> headerMap) {
        if(headerMap != null){
            for (String key: headerMap.keySet()){
                if (headerMap.get(key) != null) {
                    conn.setRequestProperty(key, String.valueOf(headerMap.get(key)));
                }
            }
        }
    }

    /**
     * map to json
     * @param params
     * @return
     */
    private String getPostBodyJsonParameMap(Map<String, Object> params) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    jsonObject.put(entry.getKey(),entry.getValue());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * Post form body
     */
    private String getPostBodyFormParameMap(Map<String, Object> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    if (first) {
                        first = false;
                    } else {
                        result.append("&");
                    }
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
                }
            }
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * upload file add params
     */
    private String getParamsBytesString(Map<String, Object> paramsMap) {
        StringBuffer strBuf = new StringBuffer();
        for (String key : paramsMap.keySet()){
            strBuf.append(TWO_HYPHENS_BOUNDARY);
            strBuf.append(BOUNDARY);
            strBuf.append(LINE_END);
            strBuf.append("Content-Disposition: form-data; name=\"" + key + "\"");
            strBuf.append(LINE_END);
//            strBuf.append("Content-Type: " + "text/plain" );
//            strBuf.append(LINE_END);
//            strBuf.append("Content-Length: "+paramsMap.get(key).length());
//            strBuf.append(LINE_END);
            strBuf.append(LINE_END);
            strBuf.append(paramsMap.get(key));
            strBuf.append(LINE_END);
        }
        return strBuf.toString();
    }

    /**
     * upload file add params
     */
    private String getFileParamsString(File file, String fileKey, String fileType,String fileName) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(LINE_END);
        strBuf.append(TWO_HYPHENS_BOUNDARY);
        strBuf.append(BOUNDARY);
        strBuf.append(LINE_END);
        strBuf.append("Content-Disposition: form-data; name=\"" + fileKey + "\"; filename=\"" + (TextUtils.isEmpty(fileName)?file.getName():fileName) + "\"");
        strBuf.append(LINE_END);
        strBuf.append("Content-Type: " + fileType );
        strBuf.append(LINE_END);
        strBuf.append("Content-Lenght: "+file.length());
        strBuf.append(LINE_END);
        strBuf.append(LINE_END);
        return strBuf.toString();
    }

    /**
     * upload file
     */
    private String getFileParamsString(byte[] bytes, String fileKey, String fileType,String fileName) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(TWO_HYPHENS_BOUNDARY);
        strBuf.append(BOUNDARY);
        strBuf.append(LINE_END);
        strBuf.append("Content-Disposition: form-data; name=\"" + fileKey + "\"; filename=\"" + fileName + "\"");
        strBuf.append(LINE_END);
        strBuf.append("Content-Type: " + fileType );
        strBuf.append(LINE_END);
        strBuf.append("Content-Lenght: "+bytes.length);
        strBuf.append(LINE_END);
        strBuf.append(LINE_END);
        return strBuf.toString();
    }

    /**
     * get post body
     * @param type
     * @param params
     * @param jsonParams
     * @return
     */
    private String getPostBody(PostType type, Map<String, Object> params, String jsonParams) {
        switch (type) {
            case JSON:
                if (!TextUtils.isEmpty(jsonParams)) {
                    return jsonParams;
                } else if (params != null){
                    return getPostBodyJsonParameMap(params);
                } else {
                    return "";
                }
            case FORM:
            default:
                return getPostBodyFormParameMap(params);
        }
    }
}

package com.xs.simplehttp.api;



import com.xs.simplehttp.core.HttpException;
import com.xs.simplehttp.util.ThreadChange;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * response
 *
 * Created by xs code on 2019/3/11.
 */

public class Response {

    /**
     * success stream
     */
    private InputStream inputStream;
    /**
     * error stream
     */
    private InputStream errorStream;
    /**
     * code
     */
    private int code;
    /**
     * contentLength
     */
    private long contentLength;
    /**
     * exception
     */
    private Exception exception;
    /**
     * path
     */
    private String path;
    /**
     * response headers
     */
    private Map<String, List<String>> headerFields;
    /**
     * request
     */
    private Request request;
    /**
     * response String result
     */
    private String result;
    /**
     * response buffer
     */
    private byte[] buffer;
    /**
     * response error exception
     */
    private HttpException httpException;

    /**
     * create success response
     * @param conn
     * @param request
     * @return
     */
    public static Response create(HttpURLConnection conn,Request request) {
        Response response = new Response();
        response.request = request;
        try {
            response.code = conn.getResponseCode();
            response.contentLength = conn.getContentLength();
            if (response.isSuccessful()) {
                response.inputStream = conn.getInputStream();
            } else {
                response.errorStream = conn.getErrorStream();
            }
            response.headerFields = conn.getHeaderFields();
            response.path = conn.getURL().getPath();
        } catch (Exception e) {
            response.exception = e;
        }
        response.init();
        return response;
    }

    /**
     * create error response
     * @param conn
     * @param e
     * @param request
     * @return
     */
    public static Response create(HttpURLConnection conn, Exception e,Request request) {
        Response response = new Response();
        if(conn != null){
            conn.disconnect();
        }
        e.printStackTrace();
        response.exception = e;
        response.request = request;
        response.init();
        return response;
    }

    /**
     * init
     */
    private void init() {
        if (isSuccessful()) {
            buffer();
        } else {
            error();
        }
    }

    /**
     * response buffer
     */
    private void buffer() {
        InputStream is = null;
        byte[] buf = new byte[1024*8];
        int len = 0;
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try{
            is = inputStream;
            final long total = contentLength;
            long sum = 0;
            while ((len = is.read(buf)) != -1){
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                if (total != -1) {
                    ThreadChange.INSTANCE.execute(ThreadChange.MAIN, new Runnable() {
                        @Override
                        public void run() {
                            if (request.getDownloadListener() != null) {
                                request.getDownloadListener().onProgress(finalSum * 100.0f / total,total);
                            }
                        }
                    });
                }

            }
            fos.flush();
            buffer = fos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * error exception
     */
    private void error() {
        final String errorMessage;
        if(inputStream != null){
            errorMessage = getRetString(inputStream);
        }else if(errorStream != null) {
            errorMessage = getRetString(errorStream);
        }else if(exception != null) {
            errorMessage = exception.getMessage();
        }else {
            errorMessage = "";
        }
        this.httpException = new HttpException(errorMessage,code);
    }

    public int getCode() {
        return code;
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    public String getPath() {
        return path;
    }

    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }

    /**
     * get cookies
     * @return
     */
    public Map<String,String> getCookies() {
        Map<String,String> cookies = new HashMap<>();
        if (headerFields != null) {
            List<String> list = headerFields.get("Set-Cookie");
            if (list != null && list.size() > 0) {
                for (String cookie : list) {
                    int i = cookie.indexOf(";");
                    if (i > 0) {
                        String substring = cookie.substring(0, i);
                        String[] split = substring.split("=");
                        cookies.put(split[0],split[1]);
                    }
                }
            }
        }
        return cookies;
    }

    public HttpException getError(){
        return httpException;
    }

    /**
     * response String
     * @return
     */
    public String getResponseString () {
        if (result == null) {
            result =  getResponseString(buffer);
        }
        return result;
    }

    /**
     * response inputstream
     * @return
     */
    public InputStream getResponseInputStream () {
        if (buffer == null) {
            return null;
        }
        return new ByteArrayInputStream(buffer);
    }

    /**
     * response file
     * @param destFileDir
     * @param destFileName
     * @return
     */
    public File getResponseFile(String destFileDir, String destFileName){
        return getResponseFile(destFileDir,destFileName,false);
    }

    /**
     * response file
     * @param destFileDir
     * @param destFileName
     * @param append append file
     * @return
     */
    public File getResponseFile(String destFileDir, String destFileName, boolean append) {
            InputStream is = getResponseInputStream();
            byte[] buf = new byte[1024*8];
            int len = 0;
            FileOutputStream fos = null;
            try{
                File dir = new File(destFileDir);
                if (!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);
                fos = new FileOutputStream(file,append);
                while ((len = is.read(buf)) != -1){
                    fos.write(buf, 0, len);
                }
                fos.flush();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                try {
                    if (is != null) is.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null) fos.close();
                } catch (IOException e) {
                }

            }
            return null;

    }

    /**
     * inputstream convert string
     * @param is
     * @return
     */
    private static String getRetString(InputStream is) {
        String buf;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            buf = sb.toString();
            return buf;

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * get response String
     * @param buffer
     * @return
     */
    private static String getResponseString(byte[] buffer) {
        if (buffer == null) {
            return null;
        }
        return new String(buffer);
    }

}

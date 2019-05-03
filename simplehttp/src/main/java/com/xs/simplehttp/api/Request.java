package com.xs.simplehttp.api;



import com.xs.simplehttp.core.MethodType;
import com.xs.simplehttp.core.PostType;

import java.util.Map;

/**
 * request
 *
 * Created by xs code on 2019/3/12.
 */

public class Request {

    /**
     * path
     */
    private String path;
    /**
     * headers
     */
    private Map<String, Object> header;
    /**
     * params
     */
    private Map<String, Object> params;
    /**
     * json param
     */
    private String jsonParams;
    /**
     * multipart
     */
    private Multiparts multipart;
    /**
     * GET/POST/UPLOAD
     */
    private MethodType methodType;
    /**
     * JSON/FORM
     */
    private PostType postType;
    /**
     * download progress listener
     */
    private ProgressListener downloadListener;
    /**
     * upload progress listener
     */
    private ProgressListener uploadListener;

    public Request(String path, Map<String, Object> params,String jsonParams,
                   Map<String, Object> header, Multiparts multipart,
                   MethodType methodType, PostType postType,
                   ProgressListener uploadListener,ProgressListener downloadListener) {
        this.path = path;
        this.jsonParams = jsonParams;
        this.params = params;
        this.header = header;
        this.multipart = multipart;
        this.methodType = methodType;
        this.postType = postType;
        this.uploadListener = uploadListener;
        this.downloadListener = downloadListener;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getJsonParams() {
        return jsonParams;
    }

    public Map<String, Object> getHeader() {
        return header;
    }

    public Multiparts getMultipart() {
        return multipart;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public PostType getPostType() {
        return postType;
    }

    public ProgressListener getDownloadListener() {
        return downloadListener;
    }

    public ProgressListener getUploadListener() {
        return uploadListener;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }

    public void setMultipart(Multiparts multipart) {
        this.multipart = multipart;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public void setDownloadListener(ProgressListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void setUploadListener(ProgressListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public static class Builder {
        private String path;
        private String jsonParams;
        private Map<String, Object> params;
        private Map<String, Object> header;
        private Multiparts multipart;
        private MethodType methodType;
        private PostType postType;
        private ProgressListener downloadlistener;
        private ProgressListener uploadlistener;

        /**
         * build path
         * @param path
         * @return
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * header
         * @param header
         * @return
         */
        public Builder header(Map<String, Object> header) {
            this.header = header;
            return this;
        }

        /**
         * upload progress
         * @param listener
         * @return
         */
        public Builder uploadProgress(ProgressListener listener) {
            this.uploadlistener = listener;
            return this;
        }

        /**
         * download progress
         * @param listener
         * @return
         */
        public Builder downloadProgress(ProgressListener listener) {
            this.downloadlistener = listener;
            return this;
        }

        /**
         * Get
         * @return
         */
        public Builder get() {
            this.methodType = MethodType.GET;
            return this;
        }

        /**
         * Get
         * @param params
         * @return
         */
        public Builder get(Map<String, Object> params) {
            this.params = params;
            this.methodType = MethodType.GET;
            return this;
        }

        /**
         * Post application/json
         * @param params
         * @return
         */
        public Builder postJson(Map<String, Object> params) {
            this.params = params;
            this.methodType = MethodType.POST;
            this.postType = PostType.JSON;
            return this;
        }

        /**
         * Post application/json
         * @param jsonParams
         * @return
         */
        public Builder postJson(String jsonParams) {
            this.jsonParams = jsonParams;
            this.methodType = MethodType.POST;
            this.postType = PostType.JSON;
            return this;
        }

        /**
         * Post application/x-www-form-urlencoded
         * @param params
         * @return
         */
        public Builder postForm(Map<String, Object> params) {
            this.params = params;
            this.methodType = MethodType.POST;
            this.postType = PostType.FORM;
            return this;
        }

        /**
         * Post
         * @return
         */
        public Builder post() {
            this.methodType = MethodType.POST;
            this.postType = PostType.FORM;
            return this;
        }

        /**
         * upload
         * @param multipart
         * @param params
         * @return
         */
        public Builder upload(Multiparts multipart, Map<String, Object> params) {
            this.methodType = MethodType.UPLOAD;
            this.params = params;
            this.multipart = multipart;
            return this;
        }

        public Request build() {
            return new Request(path,params,jsonParams,header,multipart,methodType,postType,uploadlistener,downloadlistener);
        }
    }
}

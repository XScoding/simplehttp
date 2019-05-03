package com.xs.simplehttp.api;


import android.text.TextUtils;


import com.xs.simplehttp.util.FileExtension;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传文件封装类
 *
 * Created by xs code on 2019/3/12.
 */

public class Multiparts {

    public static final String FILE_TYPE_FILE = "file/*";
    public static final String FILE_TYPE_IMAGE = "image/*";
    public static final String FILE_TYPE_AUDIO = "audio/*";
    public static final String FILE_TYPE_VIDEO = "video/*";

    private List<Part> parts = new ArrayList<>();

    public List<Part> getParts() {
        return parts;
    }

    public void add(Part part) {
        parts.add(part);
    }

    public static class Part{

        /**
         * Content-Disposition: form-data; name="{fileKey}"; filename="{fileName}"
         */
        private String fileKey;
        /**
         * Content-Type: {fileType}
         */
        private String fileType;
        /**
         * Content-Disposition: form-data; name="{fileKey}"; filename="{fileName}"
         */
        private String fileName;

        /**
         * file
         */
        private File file;
        /**
         * file bytes
         */
        private byte[] bytes;

        public Part(File file) {
            String fileName = file.getName();
            String fileType = Multiparts.getFileType(fileName);
            this.file = file;
            this.fileKey = "file";
            this.fileName = fileName;
            this.fileType = fileType;
        }

        public Part(byte[] bytes) {
            String fileName = Multiparts.getFileName(bytes);
            String fileType = Multiparts.getFileType(fileName);
            this.bytes = bytes;
            this.fileKey = "file";
            this.fileName = fileName;
            this.fileType = fileType;
        }

        public Part(File file, String fileKey, String fileType, String fileName) {
            this.fileKey = fileKey;
            this.fileType = fileType;
            this.fileName = fileName;
            this.file = file;
        }


        public Part(byte[] bytes, String fileKey, String fileType, String fileName) {
            this.fileKey = fileKey;
            this.fileType = fileType;
            this.fileName = fileName;
            this.bytes = bytes;
        }

        public String getFileKey() {
            return fileKey;
        }

        public String getFileType() {
            return fileType;
        }

        public String getFileName() {
            return fileName;
        }

        public File getFile() {
            return file;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

    /**
     * get fileType
     * @param fileName
     * @return
     */
    public static String getFileType(String fileName) {
        String fileType = URLConnection.guessContentTypeFromName(fileName);
        if (TextUtils.isEmpty(fileType)) {
            fileType = FILE_TYPE_FILE;
        }
        return fileType;
    }

    /**
     * file bytes get fileName
     * @param bytes
     * @return
     */
    public static String getFileName(byte[] bytes) {
        String fileExt = FileExtension.getFileType(bytes);//文件后缀
        String fileName = "upload_" + System.currentTimeMillis() + (fileExt.isEmpty()?"":"." + fileExt);
        return fileName;
    }
}

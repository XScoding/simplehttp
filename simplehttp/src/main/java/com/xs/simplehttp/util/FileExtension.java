package com.xs.simplehttp.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * file extension util
 *
 * Created by xs code on 2019/2/28.
 */

public class FileExtension {

    // 缓存文件头信息-文件头信息
    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();

    static {
        // images
        mFileTypes.put("FFD8FF", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");
        //
        mFileTypes.put("41433130", "dwg"); // CAD
        mFileTypes.put("38425053", "psd");
        mFileTypes.put("7B5C727466", "rtf"); // 日记本
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        mFileTypes.put("44656C69766572792D646174653A", "eml"); // 邮件
        mFileTypes.put("D0CF11E0", "doc");
        mFileTypes.put("5374616E64617264204A", "mdb");
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462D312E", "pdf");
        mFileTypes.put("504B03040A00000000008", "docx");
        mFileTypes.put("504B0304", "zip");// zip 压缩文件
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("1F8B08", "gz");
    }

    /**
     * 根据文件路径获取文件类型
     *
     * @param filePath 文件路径
     * @return 文件类型
     */
    public static String getFileType(String filePath) {
        String value = getFileHeader(filePath);
        String result = "";
        //mp4格式头比较特殊，从第4个字节开始才是mp4固有的头 0000001C667479706D70  00000020667479706d70 00000018667479706d70也是mp4
        if (value.contains("667479706d70")) {
            return "mp4";
        }
        for (Map.Entry<String, String> entry : mFileTypes.entrySet()) {
            if (value.startsWith(entry.getKey())) {
                result = entry.getValue();
            }
        }
        return result;
    }

    /**
     * 根据文件字节获取文件类型
     *
     * @return 文件类型
     */
    public static String getFileType(byte[] bytes) {
        String value = getFileHeader(bytes);
        String result = "";
        //mp4格式头比较特殊，从第4个字节开始才是mp4固有的头 0000001C667479706D70  00000020667479706d70 00000018667479706d70也是mp4
        if (value.contains("667479706d70")) {
            return "mp4";
        }
        for (Map.Entry<String, String> entry : mFileTypes.entrySet()) {
            if (value.contains(entry.getKey())) {
                result = entry.getValue();
            }
        }
        return result;
    }

    /**
     * 根据文件路径获取文件头信息
     *
     * @param filePath 文件路径
     * @return 文件头信息
     */
    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = "";
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[20];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     * 根据文件字节获取文件头信息
     *
     * @return 文件头信息
     */
    public static String getFileHeader(byte[] bytes) {
        String value = "";
        if (bytes.length > 20) {
            byte[] b = Arrays.copyOf(bytes, 20);
            value = bytesToHexString(b);
        }
        return value;
    }

    /**
     * 将要读取文件头信息的文件的byte数组转换成string类型表示
     *
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头十六进制信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        System.out.println("HexString: " + builder.toString());
        return builder.toString();
    }
}

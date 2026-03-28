package com.floyd.core.util;

import com.floyd.core.PluginBizException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * @author floyd
 * @date 2026/3/24
 */
public class FileUtil {

    /**
     * 读取文件内容
     *
     * @param file
     * @param charset
     * @return
     */
    public static String readString(File file, Charset charset) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            int readLength = fis.read(buffer);
            if (readLength != buffer.length) {
                throw new RuntimeException("文件长度异常");
            }
            return new String(buffer, charset);
        } catch (Exception e) {
            throw new PluginBizException(e);
        }
    }

    /**
     * 写入文件内容
     *
     * @param file
     * @param content
     * @param charset
     */
    public static void writeString(File file, String content, Charset charset) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(charset));
        } catch (Exception e) {
            throw new PluginBizException(e);
        }
    }
}

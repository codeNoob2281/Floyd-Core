package com.floyd.core.util;

import com.floyd.core.PluginBizException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author floyd
 * @date 2026/3/24
 */
public class FileUtil {

    public static final int BUFFER_SIZE = 8192;

    /**
     * Read file content
     *
     * @param file
     * @param charset
     * @return
     */
    public static String readString(File file, Charset charset) {
        try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toString(charset);
        } catch (Exception e) {
            throw new PluginBizException(e);
        }
    }

    /**
     * Write content to file
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

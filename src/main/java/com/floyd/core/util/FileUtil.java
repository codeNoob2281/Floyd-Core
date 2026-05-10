package com.floyd.core.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author floyd
 */
public class FileUtil {

    public static final int BUFFER_SIZE = 8192;

    /**
     * Read file content
     *
     * @param file
     * @param charset
     * @return file content
     * @throws IOException if an I/O error occurs
     */
    public static String readString(File file, Charset charset) throws IOException {
        try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toString(charset);
        }
    }

    /**
     * Write content to file
     *
     * @param file    target file
     * @param content to write
     * @param charset charset
     * @throws IOException if an I/O error occurs
     */
    public static void writeString(File file, String content, Charset charset) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(charset));
        }
    }

    /**
     * Get file suffix
     *
     * @param filename file name
     * @return file suffix
     */
    public static String getFileNameSuffix(String filename) {
        if (filename == null) {
            return null;
        }
        int idx = filename.lastIndexOf(".");
        if (idx == -1) {
            return null;
        }
        return filename.substring(idx + 1);
    }
}

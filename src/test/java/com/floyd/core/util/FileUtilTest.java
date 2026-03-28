package com.floyd.core.util;

import com.floyd.core.PluginBizException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileUtil 单元测试
 *
 * @author floyd
 */
class FileUtilTest {

    @TempDir
    File tempDir;

    @Test
    void testReadString_WithUTF8Charset() throws IOException {
        String content = "Hello, World! 你好，世界！";
        File file = createTempFile("test_utf8.txt", content);

        String result = FileUtil.readString(file, StandardCharsets.UTF_8);

        assertEquals(content, result);
    }

    @Test
    void testReadString_WithGBKCharset() throws IOException {
        String content = "测试 GBK 编码";
        File file = createTempFile("test_gbk.txt", content);

        String result = FileUtil.readString(file, Charset.forName("GBK"));

        assertNotNull(result);
    }

    @Test
    void testReadString_WithEmptyFile() throws IOException {
        File file = new File(tempDir, "empty.txt");
        file.createNewFile();

        String result = FileUtil.readString(file, StandardCharsets.UTF_8);

        assertEquals("", result);
    }

    @Test
    void testReadString_WithNonExistentFile() {
        File nonExistentFile = new File(tempDir, "not_exists.txt");

        assertThrows(PluginBizException.class, () -> {
            FileUtil.readString(nonExistentFile, StandardCharsets.UTF_8);
        });
    }

    @Test
    void testReadString_WithSpecialCharacters() throws IOException {
        String content = "特殊字符：\n\t\r\\\"'<>{}[]&$#@!~`|^*()+=-";
        File file = createTempFile("test_special.txt", content);

        String result = FileUtil.readString(file, StandardCharsets.UTF_8);

        assertEquals(content, result);
    }

    @Test
    void testReadString_WithMultilineContent() throws IOException {
        String content = "第一行\n第二行\n第三行\n第四行";
        File file = createTempFile("test_multiline.txt", content);

        String result = FileUtil.readString(file, StandardCharsets.UTF_8);

        assertEquals(content, result);
    }

    @Test
    void testReadString_WithLargeFile() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Line ").append(i).append("\n");
        }
        String content = sb.toString();
        File file = createTempFile("test_large.txt", content);

        String result = FileUtil.readString(file, StandardCharsets.UTF_8);

        assertEquals(content, result);
    }

    @Test
    void testWriteString_WithUTF8Charset() throws IOException {
        String content = "写入测试内容 Hello World!";
        File file = new File(tempDir, "test_write_utf8.txt");

        FileUtil.writeString(file, content, StandardCharsets.UTF_8);

        assertTrue(file.exists());
        String readContent = FileUtil.readString(file, StandardCharsets.UTF_8);
        assertEquals(content, readContent);
    }

    @Test
    void testWriteString_WithOverwriteExistingFile() throws IOException {
        File file = createTempFile("test_overwrite.txt", "原始内容");
        String newContent = "新内容";

        FileUtil.writeString(file, newContent, StandardCharsets.UTF_8);

        String readContent = FileUtil.readString(file, StandardCharsets.UTF_8);
        assertEquals(newContent, readContent);
    }

    @Test
    void testWriteString_WithEmptyContent() throws IOException {
        File file = new File(tempDir, "test_write_empty.txt");

        FileUtil.writeString(file, "", StandardCharsets.UTF_8);

        assertTrue(file.exists());
        String readContent = FileUtil.readString(file, StandardCharsets.UTF_8);
        assertEquals("", readContent);
    }

    @Test
    void testWriteString_WithNullCharset() throws IOException {
        File file = new File(tempDir, "test_write_null_charset.txt");

        assertThrows(PluginBizException.class, () -> {
            FileUtil.writeString(file, "内容", null);
        });
    }

    private File createTempFile(String fileName, String content) throws IOException {
        File file = new File(tempDir, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }
}

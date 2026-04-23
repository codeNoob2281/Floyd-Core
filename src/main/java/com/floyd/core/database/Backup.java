package com.floyd.core.database;

import com.floyd.core.database.fields.*;
import com.floyd.core.database.syntax.Insert;
import com.floyd.core.database.syntax.Select;
import com.floyd.core.database.syntax.show.Show;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class Backup {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(Backup.class);

    public static void exportCsv(String tableName, File file, String orderKey) throws SQLException, IOException {
        StringBuilder builder = new StringBuilder();
        Map<String, Field<?>> fields = Show.show().columns().from(tableName).execute();
        for (Map.Entry<String, Field<?>> entry : fields.entrySet()) {
            builder.append(entry.getKey()).append(",");
        }
        builder.deleteCharAt(builder.length() - 1).append("\n");
        for (Map.Entry<String, Field<?>> entry : fields.entrySet()) {
            builder.append(entry.getValue().getUnifyTypeStr()).append(",");
        }
        builder.deleteCharAt(builder.length() - 1).append("\n");
        Field<?>[] columns = fields.values().toArray(new Field[0]);
        List<Map<String, Field<?>>> rows = Select
                .select(columns)
                .from(tableName)
                .ascend(orderKey)
                .execute();
        for (Map<String, Field<?>> row : rows) {
            for (Map.Entry<String, Field<?>> entry : row.entrySet()) {
                String value = entry.getValue().getValue().toString();
                if (value.contains(",")) {
                    value = "\"" + value.replace("\"", "\"\"") + "\""; // Escape quotes
                }
                builder.append(value).append(",");
            }
            builder.deleteCharAt(builder.length() - 1).append("\n");
        }
        // Write the builder to the file
        Files.write(file.toPath(), builder.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void importCsv(String tableName, File file, String key) throws IOException, SQLException {
        logger.warn("Importing " + tableName + " from " + file.getAbsolutePath());
        String content = Files.readString(file.toPath());
        String[] lines = content.split("\n");
        String[] columnsStr = lines[0].split(",");
        String[] types = lines[1].split(",");
        for (int i = 2; i < lines.length; i++) {
            String[] valuesStr = new String[columnsStr.length];
            int flag = 0;
            StringBuilder valuesBuilder = new StringBuilder();
            boolean inQuotes = false;
            for (int j = 0; j < lines[i].length(); j++) {
                char c = lines[i].charAt(j);
                if (c == '"') {
                    if (inQuotes && j + 1 < lines[i].length() && lines[i].charAt(j + 1) == '"') {
                        // 处理转义引号
                        valuesBuilder.append('"');
                        j++;
                    } else {
                        inQuotes = !inQuotes;
                    }
                } else if (c == ',' && !inQuotes) {
                    valuesStr[flag++] = valuesBuilder.toString().trim();
                    valuesBuilder.setLength(0);
                } else {
                    valuesBuilder.append(c);
                }
            }
            // 处理最后一个字段
            if (flag < columnsStr.length) {
                valuesStr[flag] = valuesBuilder.toString().trim();
            }
            Field<?>[] fields = new Field[columnsStr.length];
            for (int j = 0; j < columnsStr.length; j++) {
                String columnStr = columnsStr[j].trim();
                String type = types[j].trim();
                if (type.equals(new FieldBoolean("").getUnifyTypeStr())) {
                    fields[j] = new FieldBoolean(columnStr, Boolean.parseBoolean(valuesStr[j].trim()));
                } else if (type.equals(new FieldFloat("").getUnifyTypeStr())) {
                    fields[j] = new FieldFloat(columnStr, Float.parseFloat(valuesStr[j].trim()));
                } else if (type.equals(new FieldInteger("").getUnifyTypeStr())) {
                    fields[j] = new FieldInteger(columnStr, Integer.parseInt(valuesStr[j].trim()));
                } else if (type.equals(new FieldLong("").getUnifyTypeStr())) {
                    fields[j] = new FieldLong(columnStr, Long.parseLong(valuesStr[j].trim()));
                } else if (type.equals(new FieldString("").getUnifyTypeStr())) {
                    fields[j] = new FieldString(columnStr, valuesStr[j].trim());
                } else if (type.equals(new FieldTimestamp("").getUnifyTypeStr())) {
                    fields[j] = new FieldTimestamp(columnStr, java.sql.Timestamp.valueOf(valuesStr[j].trim()));
                } else {
                    throw new SQLException("Unsupported type: " + type + " for importing");
                }
            }
            Insert.insert()
                    .into(tableName)
                    .values(fields)
                    .onConflict(key)
                    .doNothing()
                    .execute();
            String progress = "Importing " + tableName + " " + (i - 2) + "/" + (lines.length - 2) + "\t\tProgress: " + (i - 2) * 100f / (lines.length - 2) + "%";
            if ((i - 2) % 100 == 1) {
                logger.warn(progress);
            }
            if (i == lines.length - 1) {
                logger.warn(progress);
                logger.warn("Importing " + tableName + " finished");
            }
        }
    }

}

package com.floyd.core.database;

import com.floyd.core.database.fields.*;
import com.floyd.core.database.syntax.Insert;
import com.floyd.core.database.syntax.Select;
import com.floyd.core.database.syntax.show.Show;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            Map<String, Field<?>> fields = Show.show().columns().from(tableName).execute();
            // Write column names
            boolean first = true;
            for (Map.Entry<String, Field<?>> entry : fields.entrySet()) {
                if (!first) {
                    writer.write(',');
                }
                writer.write(entry.getKey());
                first = false;
            }
            writer.write('\n');
            // Write types
            first = true;
            for (Map.Entry<String, Field<?>> entry : fields.entrySet()) {
                if (!first) {
                    writer.write(',');
                }
                writer.write(entry.getValue().getUnifyTypeStr());
                first = false;
            }
            writer.write('\n');
            // Write data rows
            Field<?>[] columns = fields.values().toArray(new Field[0]);
            List<Map<String, Field<?>>> rows = Select
                    .select(columns)
                    .from(tableName)
                    .ascend(orderKey)
                    .execute();
            for (Map<String, Field<?>> row : rows) {
                first = true;
                for (Map.Entry<String, Field<?>> entry : row.entrySet()) {
                    if (!first) {
                        writer.write(',');
                    }
                    String value = entry.getValue().getValue().toString();
                    if (value.contains(",") || value.contains("\"")) {
                        value = "\"" + value.replace("\"", "\"\"") + "\"";
                    }
                    writer.write(value);
                    first = false;
                }
                writer.write('\n');
            }
        }
    }

    public static void importCsv(String tableName, File file, String key) throws IOException, SQLException {
        logger.warn("Importing " + tableName + " from " + file.getAbsolutePath());
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            // Read column names from first row
            String line = reader.readLine();
            if (line == null) {
                logger.warn("Importing " + tableName + " finished (empty file)");
                return;
            }

            // Read types from second row
            String[] columnsStr = line.split(",");
            line = reader.readLine();
            if (line == null) {
                logger.warn("Importing " + tableName + " finished (missing type line)");
                return;
            }
            String[] types = line.split(",");


            // Read data rows
            int rowCount = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] valuesStr = new String[columnsStr.length];
                int flag = 0;
                StringBuilder valuesBuilder = new StringBuilder();
                boolean inQuotes = false;
                for (int j = 0; j < line.length(); j++) {
                    char c = line.charAt(j);
                    if (c == '"') {
                        if (inQuotes && j + 1 < line.length() && line.charAt(j + 1) == '"') {
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

                rowCount++;
                if (rowCount % 100 == 1) {
                    logger.warn("Importing " + tableName + " " + (rowCount - 1) + " rows processed");
                }
            }

            logger.warn("Importing " + tableName + " " + rowCount + " rows processed\t\tProgress: 100%");
            logger.warn("Importing " + tableName + " finished");
        }
    }

}

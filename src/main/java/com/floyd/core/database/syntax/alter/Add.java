package com.floyd.core.database.syntax.alter;


import com.floyd.core.database.DatabaseManager;
import com.floyd.core.database.syntax.show.Show;
import com.floyd.core.database.syntax.table.Column;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Add extends Alter {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(Add.class);

    private Column column;

    public Add column(Column column) {
        this.column = column;
        return this;
    }

    @Override
    public String getSql() {
        return "ALTER TABLE " + tableName + " ADD COLUMN " + column.getSql();
    }

    public void execute() throws SQLException {
        try (Connection conn = DatabaseManager.instance.getConnection()) {
            if (Show.show().columns().from(tableName).execute().containsKey(column.getColumn().getName())) {
                return;
            }
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(getSql());
        } catch (SQLException e) {
            logger.error("SQL: " + getSql());
            logger.error("执行SQL异常", e);
            throw new SQLException("Error executing delete statement: " + e.getMessage(), e);
        }
    }
}

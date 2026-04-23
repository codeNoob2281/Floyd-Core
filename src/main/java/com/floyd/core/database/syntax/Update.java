package com.floyd.core.database.syntax;

import com.floyd.core.database.DatabaseManager;
import com.floyd.core.database.fields.Field;
import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public abstract class Update implements Syntax {

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(Update.class);

    protected final String tableName;
    protected Field<?>[] fields;
    protected String where;
    protected Object[] whereArgs;

    /**
     * Factory method to create a new Update instance based on the database type.
     * <p>
     * Example usage:
     * <pre>
     *     Update update = Update.update("table_name")
     *        .set(field1, field2)
     *        .where("column1 = ?", "value");
     * </pre>
     *
     * @param tableName the name of the table to update
     * @return a new instance of Update implementation specific to the database type
     * @throws UnsupportedOperationException if the database type is not supported
     */
    public static Update update(String tableName) {
        return switch (DatabaseManager.instance.getType()) {
            case SQLITE -> new sqlite_impl(tableName);
            case MYSQL -> new mysql_impl(tableName);
            case PGSQL -> new pgsql_impl(tableName);
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with UPDATE"
            );
        };
    }

    private Update(String tableName) {
        this.tableName = tableName;
    }

    public Update set(Field<?>... fields) {
        this.fields = fields;
        return this;
    }

    public Update where(String conditions, Object... args) {
        this.where = conditions;
        this.whereArgs = args;
        return this;
    }

    /**
     * Executes the update statement.
     *
     * @return the number of rows affected by the update
     * @throws SQLException if a database access error occurs
     */
    public int execute() throws SQLException {
        try (Connection conn = DatabaseManager.instance.getConnection()) {
            String sql = getSql();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            int index = 1;
            for (Field<?> field : fields) {
                preparedStatement.setObject(index++, field.getValue());
            }
            for (Object arg : whereArgs) {
                preparedStatement.setObject(index++, arg);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL: " + getSql());
            logger.error("SET Param: " + Arrays.stream(fields).map(Field::getValue));
            logger.error("WHERE Param: " + List.of(whereArgs));
            logger.error(e);
            throw new SQLException("Error executing update: " + e.getMessage(), e);
        }
    }

    // Implementation of jdbc methods

    private static class sqlite_impl extends Update {
        private sqlite_impl(String tableName) {
            super(tableName);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            for (Field<?> field : fields) {
                sql.append(field.getName()).append(" = ?, ");
            }
            sql.delete(sql.length() - 2, sql.length());
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }

    private static class mysql_impl extends Update {
        private mysql_impl(String tableName) {
            super(tableName);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            for (Field<?> field : fields) {
                sql.append(field.getName()).append(" = ?, ");
            }
            sql.delete(sql.length() - 2, sql.length());
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }

    private static class pgsql_impl extends Update {
        private pgsql_impl(String tableName) {
            super(tableName);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            for (Field<?> field : fields) {
                sql.append(field.getName()).append(" = ?, ");
            }
            sql.delete(sql.length() - 2, sql.length());
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }
}

package com.floyd.core.database.syntax.alter;

import com.floyd.core.database.syntax.Syntax;

public class Alter implements Syntax {

    protected String tableName;

    public static Alter alter() {
        return new Alter();
    }

    protected Alter() {
    }

    public Alter table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Add add() {
        return (Add) new Add().table(tableName);
    }

    public Drop drop() {
        return (Drop) new Drop().table(tableName);
    }

    @Override
    public String getSql() {
        return "";
    }
}

package com.floyd.core.database.syntax.show;

import com.floyd.core.database.syntax.Syntax;

public class Show implements Syntax {

    public static Show show() {
        return new Show();
    }

    protected Show() {
    }

    public Tables tables() {
        return Tables.showTables();
    }

    public Columns columns() {
        return Columns.showColumns();
    }

    @Override
    public String getSql() {
        return "";
    }
}

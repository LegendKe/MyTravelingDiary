package com.ruihai.xingka.db;

import com.ruihai.xingka.XKApplication;

import org.aisen.orm.SqliteUtility;
import org.aisen.orm.SqliteUtilityBuilder;

/**
 * Created by zecker on 15/10/2.
 */
public class XingKaDB {

    public static SqliteUtility getSqlite() {
        if (SqliteUtility.getInstance("xingka_db") == null) {
            new SqliteUtilityBuilder().configDBName("xingka_db")
                    .configVersion(1)
                    .build(XKApplication.getInstance());
        }
        return SqliteUtility.getInstance("xingka_db");
    }
}

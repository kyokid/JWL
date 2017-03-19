package jwl.fpt.util;

import org.hibernate.dialect.PostgreSQL82Dialect;

/**
 * Created by thiendn on 20/03/2017.
 */
public class MyPostgreSQLDialect extends PostgreSQL82Dialect {
    public MyPostgreSQLDialect() {
//        super();
        // "trgm_match" is the name of the function to use in hql queries
        registerFunction("trgm_match", new PostgreSQLTrigramFunction());
    }
}

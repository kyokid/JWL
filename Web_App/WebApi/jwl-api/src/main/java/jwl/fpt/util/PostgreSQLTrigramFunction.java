package jwl.fpt.util;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

import java.util.List;

/**
 * Created by thiendn on 20/03/2017.
 */
public class PostgreSQLTrigramFunction implements SQLFunction {
    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }

    @Override
    public Type getReturnType(Type type, Mapping mapping) throws QueryException {
        return new BooleanType();
    }

    @Override
    public String render(Type type, List list, SessionFactoryImplementor sessionFactoryImplementor) throws QueryException {
        if (list.size() != 2) {
            throw new IllegalArgumentException(
                    "The function must be passed 2 arguments");
        }
        String field = (String) list.get(0);
        String value = (String) list.get(1);
        // returns the string that will replace your function
        // in the sql script
        return "similarity(" + field + "," + value + ")";
//        return field + " % " + value + "";
    }
}

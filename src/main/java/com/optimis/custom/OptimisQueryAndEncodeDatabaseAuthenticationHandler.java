package com.optimis.custom;

import org.jasig.cas.adaptors.jdbc.QueryAndEncodeDatabaseAuthenticationHandler;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by nex on 8.1.16..
 */
public class OptimisQueryAndEncodeDatabaseAuthenticationHandler extends QueryAndEncodeDatabaseAuthenticationHandler {

    public OptimisQueryAndEncodeDatabaseAuthenticationHandler(final DataSource datasource,
                                                              final String sql,
                                                              final String algorithmName) {
        super(datasource, sql, algorithmName);
    }

    @Override
    protected String digestEncodedPassword(final String encodedPassword, final Map<String, Object> values) {
        final OptimisPasswordEncoder encoder = new OptimisPasswordEncoder();

        Long numOfIterations = this.numberOfIterations;
        if (values.containsKey(this.numberOfIterationsFieldName)) {
            final String longAsStr = values.get(this.numberOfIterationsFieldName).toString();
            numOfIterations = Long.valueOf(longAsStr);
        }

        if (!values.containsKey(this.saltFieldName)) {
            throw new RuntimeException("Specified field name for salt does not exist in the results");
        }
        final String dynaSalt = values.get(this.saltFieldName).toString();
        final String source = new StringBuilder(encodedPassword).append(dynaSalt).toString();
        return encoder.encode(source, numOfIterations.intValue());
    }
}

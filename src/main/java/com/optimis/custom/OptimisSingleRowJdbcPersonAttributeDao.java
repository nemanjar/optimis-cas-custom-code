package com.optimis.custom;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.CaseInsensitiveAttributeNamedPersonImpl;
import org.jasig.services.persondir.support.CaseInsensitiveNamedPersonImpl;
import org.jasig.services.persondir.support.MultivaluedPersonAttributeUtils;
import org.jasig.services.persondir.support.jdbc.SingleRowJdbcPersonAttributeDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by nex on 10.1.16..
 */
public class OptimisSingleRowJdbcPersonAttributeDao extends SingleRowJdbcPersonAttributeDao {

    private String extraAttributesSql;
    private Set<String> uuidAttributes;
    private JdbcTemplate jdbcTemplate;
    private String abilitiesSql;

    public OptimisSingleRowJdbcPersonAttributeDao(final DataSource ds, final String sql) {
        super(ds, sql);
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    protected List<IPersonAttributes> parseAttributeMapFromResults(final List<Map<String, Object>> queryResults, final String queryUserName) {
        final List<IPersonAttributes> peopleAttributes = new ArrayList<>(queryResults.size());

        for (final Map<String, Object> queryResult : queryResults) {
            UUIDUtil.fixUuidAttributes(queryResult, uuidAttributes);
            final Map<String, List<Object>> multivaluedQueryResult = MultivaluedPersonAttributeUtils.toMultivaluedMap(queryResult);
            List memberships = resolveExtraAttributes(queryUserName);
            // Wrap resulting attributes in a list in case there is only one element inside.
            // PrincipalResolver will change resulting type in that case
            List membershipsWrapper = new ArrayList();
            membershipsWrapper.add(memberships);
            multivaluedQueryResult.put("memberships",membershipsWrapper );
            final IPersonAttributes person;
            final String userNameAttribute = this.getConfiguredUserNameAttribute();
            if (this.isUserNameAttributeConfigured() && queryResult.containsKey(userNameAttribute)) {
                // Option #1:  An attribute is named explicitly in the config,
                // and that attribute is present in the results from LDAP;  use it
                person = new CaseInsensitiveAttributeNamedPersonImpl(userNameAttribute, multivaluedQueryResult);
            } else if (queryUserName != null) {
                // Option #2:  Use the userName attribute provided in the query
                // parameters.  (NB:  I'm not entirely sure this choice is
                // preferable to Option #3.  Keeping it because it most closely
                // matches the legacy behavior there the new option -- Option #1
                // -- doesn't apply.  ~drewwills)
                person = new CaseInsensitiveNamedPersonImpl(queryUserName, multivaluedQueryResult);
            } else {
                // Option #3:  Create the IPersonAttributes doing a best-guess
                // at a userName attribute
                person = new CaseInsensitiveAttributeNamedPersonImpl(userNameAttribute, multivaluedQueryResult);
            }

            peopleAttributes.add(person);
        }

        return peopleAttributes;
    }

    private List<Map<String, Object>> resolveExtraAttributes(String queryUserName){
        List<Map<String, Object>> result = null;
        try {
            result = jdbcTemplate.query(extraAttributesSql, createRowMapper(), queryUserName);
            UUIDUtil.fixUuidAttributes(result, uuidAttributes);
            for (Map<String, Object> membershipValuesMap : result) {
                Integer membershipId = (Integer) membershipValuesMap.get("id");
                List<Map<String, Object>> abilities = jdbcTemplate.query(abilitiesSql, createRowMapper(), membershipId);
                membershipValuesMap.put("abilities", abilities);
                membershipValuesMap.remove("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private RowMapper<Map<String,Object>> createRowMapper() throws SQLException{
        RowMapper<Map<String,Object>> result = new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                final ResultSetMetaData rsmd = rs.getMetaData();
                final int columnCount = rsmd.getColumnCount();
                final Map<String, Object> mapOfColValues = new HashMap<String, Object>();

                for (int i = 1; i <= columnCount; i++) {
                    final String columnName = JdbcUtils.lookupColumnName(rsmd, i);
                    final Object obj = JdbcUtils.getResultSetValue(rs, i);
                    if (obj != null) {
                        mapOfColValues.put(columnName, obj);
                    }
                }

                return mapOfColValues;
            }
        };
        return result;
    }
    public String getExtraAttributesSql() {
        return extraAttributesSql;
    }

    public void setExtraAttributesSql(String extraAttributesSql) {
        this.extraAttributesSql = extraAttributesSql;
    }

    public Set<String> getUuidAttributes() {
        return uuidAttributes;
    }

    public void setUuidAttributes(Set<String> uuidAttributes) {
        this.uuidAttributes = uuidAttributes;
    }

    public String getAbilitiesSql() {
        return abilitiesSql;
    }

    public void setAbilitiesSql(String abilitiesSql) {
        this.abilitiesSql = abilitiesSql;
    }
}

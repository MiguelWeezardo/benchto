/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.benchto.driver.queries;

import com.google.common.collect.ImmutableMap;
import io.trino.benchto.driver.BenchmarkExecutionException;
import io.trino.benchto.driver.IntegrationTest;
import io.trino.benchto.driver.Query;
import io.trino.benchto.driver.loader.QueryLoader;
import io.trino.benchto.driver.loader.SqlStatementGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueryLoaderTest
        extends IntegrationTest
{
    @Autowired
    private QueryLoader queryLoader;

    @Autowired
    private SqlStatementGenerator sqlStatementGenerator;

    @Test
    public void shouldLoadPrestoQuery()
    {
        verifySimpleSelect("presto/simple_select.sql", "simple_select", "1");
        verifySimpleSelect("presto/second_simple_select.sql", "second_simple_select", "2");
    }

    private void verifySimpleSelect(String path, String queryName, String rowValue)
    {
        Query query = queryLoader.loadFromFile(path);
        List<String> sqlStatements = sqlStatementGenerator.generateQuerySqlStatement(query, createAttributes("database", "schema"));
        assertThat(query.getName()).isEqualTo(queryName);
        assertThat(sqlStatements).containsExactly("SELECT " + rowValue + " FROM \"schema\".SYSTEM_USERS");
    }

    @Test
    public void shouldFailWhenNoQueryFile()
    {
        BenchmarkExecutionException ex = assertThrows(BenchmarkExecutionException.class, () -> queryLoader.loadFromFile("presto/non_existing_file.sql"));
        assertEquals("Could not find any SQL query file for query name: presto/non_existing_file.sql", ex.getMessage());
    }

    @Test
    public void shouldFailWhenQueryFileIsDuplicated()
    {
        BenchmarkExecutionException ex = assertThrows(BenchmarkExecutionException.class, () -> queryLoader.loadFromFile("presto/duplicate_query.sql"));
        assertEquals("Found multiple SQL query files for query name: presto/duplicate_query.sql", ex.getMessage());
    }

    @Test
    public void shouldFailsWhenRequiredAttributesAreAbsent()
    {
        Query query = queryLoader.loadFromFile("presto/simple_select.sql");
        BenchmarkExecutionException ex = assertThrows(BenchmarkExecutionException.class, () -> sqlStatementGenerator.generateQuerySqlStatement(query, emptyMap()));
    }

    private Map<String, String> createAttributes(String database, String schema)
    {
        return ImmutableMap.<String, String>builder()
                .put("database", database)
                .put("schema", schema)
                .build();
    }
}

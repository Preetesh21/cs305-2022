import java.util.List;

public interface SqlRunner {
    /**
     * Executes a select query that returns a single or no record.
     * @param queryId Unique ID of the query in the queries.xml file.
     * @param queryParam Parameter(s) to be used in the query.
     * @param resultType Type of the object that will be returned.
    after
     * populating it with the data returned by the
    SQL.
     * @return The object populated with the SQL results.
     */
    Object selectOne(String queryId, Object queryParam, Class resultType);


    /**
     * Same as {@link #selectOne(String, Object, Class)} except that
     this one
     * returns multiple rows.
     * @param queryId Unique ID of the query in the queries.xml file.
     * @param queryParam Parameter(s) to be used in the query.
     * @param resultItemType Type of the object that will be returned
     * @return a list
     */
    List<?> selectMany(String queryId, Object queryParam, Class resultItemType);


    /**
     * Execute an update statement and return the number of rows
     affected.
     * @param queryId Unique ID of the query in the queries.xml file.
     * @param queryParam Parameter(s) to be used in the query.
     * @return an int
     */int update(String queryId, Object queryParam);


    /**
     * Execute an insert statement and return the number of rows
     affected.
     * @param queryId Unique ID of the query in the queries.xml file.
     * @param queryParam Parameter(s) to be used in the query.
     * @return an int
     */
    int insert(String queryId, Object queryParam);


    /**
     * Execute a delete statement and return the number of rows
     affected.
     * @param queryId Unique ID of the query in the queries.xml file.
     * @param queryParam Parameter(s) to be used in the query.
     * @return an int
     */
    int delete(String queryId, Object queryParam);
}

import Utils.helper;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class operations implements SqlRunner{
    public Connection Connection(int a) {
        Connection connection;
        String dbURL = "jdbc:mysql://localhost:3306/sakila";
        String username = "root";
        String password = "welcome123";
        if(a==-1)
            password="wrongPassword";
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbURL, username, password);
            return connection;
        }
        catch (Exception exception) {
            System.out.println("Error in Password");
        }
        return null;
    }

    private int getRowCount(ResultSet resultSet) {
//        if (resultSet == null) {
//            return 0;
//        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            System.out.println("SQL Exception");
        }
//        } finally {
//            try {
//                resultSet.beforeFirst();
//            } catch (SQLException exp) {
//                System.out.println("SQL Exception");
//            }
//        }
        return 0;
    }

    PreparedStatement queryHelper(String sqlQuery, Object queryParamObj, Connection connection) throws SQLException, NoSuchFieldException, IllegalAccessException {
        Pattern keyToSign = Pattern.compile("(\\$\\{\\w+\\})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = keyToSign.matcher(sqlQuery);
        StringBuilder builder = new StringBuilder();
        int last = -1;
        List<Object> propValues = new ArrayList<>();
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            String match = matcher.group(1);
            int len = match.length();
            String propName = match.substring(2, len - 1);
            String tmp;
            if (last != -1) {
                assert (last != start);
                tmp = sqlQuery.substring(last + 1, start);
            } else {
                tmp = sqlQuery.substring(0, start);
            }
            builder.append(tmp);
            last = end;
            Pair data = helper.getAttribute(propName, queryParamObj);
            assert data != null;
            Object value = ((Pair<?, ?>) data).getValue();
            if (value instanceof List<?>) {
                List<?> vals = (List<?>) value;
                int countElements=vals.size();

                if (countElements == 0) {
                    builder.append(" (1=2) ");
                } else {
                    builder.append(" (");
                    for(int indx=0;indx<countElements;indx++){
                        Object val=vals.get(indx);
                        propValues.add(val.toString());
                        builder.append(" ? ");
                        if(indx+1!=countElements){
                            builder.append(" , ");
                        }
                    }
                    builder.append(") ");
                }
            } else {
                builder.append(" ? ");
                propValues.add(value.toString());
            }
        }
        if(last<sqlQuery.length())
        {
            String temp=sqlQuery.substring(last);
            builder.append(temp);
        }
        PreparedStatement statement = connection.prepareStatement(builder.toString(),ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < propValues.size(); i++) {
            statement.setObject(i + 1, propValues.get(i));
        }
        return statement;
    }

    PreparedStatement insertObjectHelper(String sqlQuery, Object queryParamObj, Connection connection) throws SQLException, NoSuchFieldException, IllegalAccessException {
        String prefix = "";
        Pattern keyToSign = Pattern.compile("(\\$\\{\\w+\\})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = keyToSign.matcher(sqlQuery);
        StringBuilder builder = new StringBuilder();
        int last = -1;
        List<Object> propValues = new ArrayList<>();
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            String match = matcher.group(1);
            int len = match.length();
            String propName = match.substring(2, len - 1);
            String tmp;
            if (last != -1) {
                // fill from [last+1....start-1]
                assert (last != start);
                tmp = sqlQuery.substring(last + 1, start);
            } else {
                // fill from [0...start-1]
                tmp = sqlQuery.substring(0, start);
            }
            builder.append(tmp);
            last = end;
            Pair data = helper.getAttribute(propName, queryParamObj);
            assert data != null;
            Object value = ((Pair<?, ?>) data).getValue();
            // add ?
            builder.append(prefix);
            prefix=",";
            builder.append(" ? ");
            propValues.add(value);
        }
        builder.append(" ) ");
        PreparedStatement statement = connection.prepareStatement(builder.toString(),ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < propValues.size(); i++) {
            statement.setObject(i + 1, propValues.get(i));
        }
        return statement;
    }

    @Override
    public Object selectOne(String queryId, Object queryParam, Class resultType) {
        // Creating Connection
        Connection con;
        con = Connection(1);
        try{
            // Based on the XML information creating the class ResultType
            Class<?> clazz ;
            clazz = (resultType);
            if (clazz == null) {
                System.out.println("class not found.");
                return null;
            }

            // Creating Object of the Class ResultType
            Object o=clazz.newInstance();

            // Create an empty hash map by declaring object
            // of string and integer type
            HashMap<String, Integer> map = new HashMap<>();
//          Getting the fields of the Object o and adding them to HashMap
//            for (Field field : queryParam.getClass().getDeclaredFields()) {
//                field.setAccessible(true);
//                String name = field.getName();
//                System.out.printf("%s", name);
//            }
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                map.put(name,1);
            }
            String[] arr;
            // Here I am getting Query and Class Name from Class Attempt(XML parsing Class)
            arr= xml_parser.find(queryId);
            // Performing the Query

            assert arr != null;
            if(queryParam==null || !queryParam.getClass().getName().equals(arr[1]))
            {
//                System.out.println(queryParam.getClass().getName()+arr[1]);
                System.out.println("Class Name MisMatch");
                return null;
            }
            PreparedStatement stat = queryHelper(arr[0],queryParam,con);
            //PreparedStatement stat=con.prepareStatement("select PersonID,LastName from Classes.Persons where PersonID =?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ResultSet resultSet;
            try {
                resultSet = stat.executeQuery();
            }catch (Exception e)
            {
                System.out.println("SQL Query Failed");
                return null;
            }
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount=getRowCount(resultSet);
            if(rowCount==0)
            {
                System.out.println("No rows found");
                return null;
            }

            if(rowCount>1)
            {
                System.out.println("Multiple Rows Selected");
                return null;
            }

            HashMap<String, Integer> map2 = new HashMap<>();
            // The column count starts from 1
            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                // Do stuff with name
                // Checking if a key is present
                if (!map.containsKey(name)) {
                    System.out.println("Column Name Issue");
                    return null;
                }
                map2.put(name,1);
            }
            // Set values to the rows of ResultType Object
            // Setting the fields in the object o
            resultSet.first();
            int i=1;
            for(Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                if(map2.containsKey(name)) {
                    int type = rsmd.getColumnType(i);
                    //System.out.println(type+name);
                    i++;
                    if (type == Types.VARCHAR || type == Types.CHAR) {
                        field.set(o, resultSet.getString(name));
                    }
                    if (type == Types.INTEGER || type == Types.SMALLINT || type == Types.TINYINT) {
                        field.set(o, resultSet.getInt(name));
                    }
                    if (type == Types.TIMESTAMP) {
                        field.set(o, resultSet.getTimestamp(name));
                    }
                }
            }
//            for (Field field : o.getClass().getDeclaredFields()) {
//                field.setAccessible(true);
//                String name = field.getName();
//                Object value = field.get(o);
//                System.out.printf("%s: %s%n", name, value);
//            }
            resultSet.close();
            stat.close();
            con.close();
            return o;
        }catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public List<?> selectMany(String queryId, Object queryParam, Class resultItemType) {
        Connection con;
        con = Connection(1);
        try {
            // Based on the XML information creating the class "org.bar.foo"
            Class<?> clazz = null;
            clazz = (resultItemType);
            if (clazz == null) {
                System.out.println("class not found");
                return null;
            }
            // Creating Object of the Class
            Object o = clazz.newInstance();
            // Create an empty hash map by declaring object
            // of string and integer type
            HashMap<String, Integer> map = new HashMap<>();
//          Getting the fields of the Object o and adding them to HashMap
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                map.put(name, 1);
            }
            String[] arr;
            // Here I am getting Query and Class Name from Class Attempt(XML parsing Class)
            arr= xml_parser.find(queryId);
            assert arr != null;
            //System.out.println(queryParam.getClass().getName()+arr[1]);
            if(queryParam==null || !queryParam.getClass().getName().equals(arr[1]))
            {
//                System.out.println(queryParam.getClass().getName()+arr[1]);
                System.out.println("Class Name MisMatch");
                return null;
            }
            PreparedStatement stat = queryHelper(arr[0],queryParam,con);
            ResultSet resultSet,rs;
            try {
                resultSet = stat.executeQuery();
            }catch (Exception e)
            {
                System.out.println("SQL Query Failed");
                return null;
            }
            rs=resultSet;

            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount = getRowCount(resultSet);
            if(rowCount==0)
            {
                System.out.println("No Rows Found");
                return null;
            }

            HashMap<String, Integer> map2 = new HashMap<>();
            List<Object> final_Result = new ArrayList<>();
            // The column count starts from 1
            for (int i = 1; i <= columnCount; i++) {
                String name = rsmd.getColumnName(i);
                // Do stuff with name
                // Checking if a key is present
                if (!map.containsKey(name)) {
                    System.out.println("Column Name Issue");
                    return null;
                }
                map2.put(name, 1);
            }
            resultSet.first();
            int j=0;

            do {
                // Creating Object of the Class
                int i = 1;
                final_Result.add(j,clazz.newInstance());

                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    String name = field.getName();
                    if (map2.containsKey(name)) {
                        int type = rsmd.getColumnType(i);
                        if (type == Types.VARCHAR || type == Types.CHAR) {
                            field.set(final_Result.get(j), rs.getString(name));
                        }
                        if (type == Types.INTEGER || type == Types.SMALLINT || type == Types.TINYINT) {
                            field.set(final_Result.get(j), rs.getInt(name));
                        }
                        if (type == Types.TIMESTAMP) {
                            field.set(o, resultSet.getTimestamp(name));
                        }
                        i++;
                    }
                }
                j++;
            }while(rs.next());

//            System.out.println("------------------");
//            for (int jj = 0; jj < rowCount; jj++){
//                for (Field field : final_Result.get(jj).getClass().getDeclaredFields()) {
//                    field.setAccessible(true);
//                    String name = field.getName();
//                    Object value = field.get(final_Result.get(jj));
//                    System.out.printf("%s: %s%n", name, value);
//                }
//            }
            resultSet.close();
            stat.close();
            con.close();
            return final_Result;
        }catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public int update(String queryId, Object queryParam) {
        // Creating Connection
        Connection con;
        con = Connection(1);
        // Getting XML information
        try{
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String[] arr;
            // Here I am getting Query and Class Name from Class Attempt(XML parsing Class)
            arr= xml_parser.find(queryId);
            assert arr != null;
            if(queryParam==null || !queryParam.getClass().getName().equals(arr[1]))
            {
//                System.out.println(queryParam.getClass().getName()+arr[1]);
                System.out.println("Class Name MisMatch");
                return -1;
            }
            PreparedStatement stat = queryHelper(arr[0],queryParam,con);
            //System.out.println(stat);
            int count;
            try {
                count = stat.executeUpdate();
            }catch (Exception e)
            {
                System.out.println("SQL Query Failed");
                return -1;
            }
            //System.out.println("The number of rows affected are:"+ count);
            statement.close();
            con.close();
            return count;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    @Override
    public int insert(String queryId, Object queryParam) {
        // Creating Connection
        Connection con;
        con = Connection(1);
        // Getting XML information
        try{
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String[] arr;
            arr= xml_parser.find(queryId);
            assert arr != null;

            // Based on the XML information creating the class QueryParam/org.foo.bar
            Class<?> clazz = null;
            try {
                clazz = Class.forName(arr[1]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz == null) {
                System.out.println("class not found");
                return -1;
            }
            if(queryParam==null || !arr[1].equals(queryParam.getClass().getName()))
            {
                System.out.println("Class MisMatch");
                return -1;
            }
            // Creating Object of the Class
            Object o=clazz.newInstance();
            clazz.cast(queryParam);
            // Setting the fields in the object o
            for(Field field : clazz.getDeclaredFields()) {
                //you can also use .toGenericString() instead of .getName(). This will
                //give you the type information as well.
                field.setAccessible(true);
                field.set(o,field.get(queryParam));
                //System.out.println(field.getName());
            }
//          Getting the fields of the Object o
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(o);
                //System.out.printf("%s: %s%n", name, value);
            }
            PreparedStatement stat = insertObjectHelper(arr[0],queryParam,con);
            int count;
            try {
                count = stat.executeUpdate();
            }catch (Exception e){
                System.out.println("SQL Query Failed");
                return -1;
            }
            //System.out.println("The number of rows affected are:"+ count);
            statement.close();
            con.close();
            return count;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(String queryId, Object queryParam) {
        // Creating Connection
        Connection con;
        con = Connection(1);

        // Getting XML information
        try{
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String[] arr;
            arr= xml_parser.find(queryId);
            assert arr != null;
            if(queryParam==null || !queryParam.getClass().getName().equals(arr[1]))
            {
                System.out.println("Class Name MisMatch");
                return -1;
            }
            PreparedStatement stat = queryHelper(arr[0],queryParam,con);
            //String query=" UPDATE Classes.Persons SET LastName=\"Perez\"  WHERE PersonID=1;";
            int count;
            try {
                count = stat.executeUpdate();
            }catch (Exception e)
            {
                System.out.println("SQL Query Failed");
                return -1;
            }
            //System.out.println("The number of rows affected are:"+ count);
            statement.close();
            con.close();
            return count;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
}
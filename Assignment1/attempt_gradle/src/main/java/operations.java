import Classes.actor;
import javafx.util.Pair;
import java.sql.Timestamp;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class operations implements SqlRunner{

    public static void main(String[] args) {
//        operations op=new operations();
//
//        Timestamp CreatedDate = Timestamp.valueOf("2022-02-10 01:42:42");
//
//        actor new_actor=new actor(100,"Sergio","Alves",CreatedDate);

//        param1 new_obj=new param1();
//        new_obj.propX= new ArrayList<>();
//        new_obj.propX.add("JOE");
//        new_obj.propY="RamosIV";
        String[] tmp={"w"};
        //new_obj.propX=new ArrayList<>();
        //new_obj.propX.add("Alves");
//        String[] names={"ED","JOE"};
//        List<String> tt;
//        tt=new ArrayList<>();
//        tt.add("ED");
        Integer variable=4;
        String a="22";
        boolean b=true;
        System.out.println(((Object)tmp).getClass().getSimpleName());
        //op.insert("addMovie", new_p);
        //op.update("letMovie",new_obj);
        //op.selectMany("findActor", names,Classes.actor.class);
    }

    public Connection Connection() {
        Connection connection;
        String dbURL = "jdbc:mysql://localhost:3306/sakila";
        String username = "root";
        String password = "welcome123";
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbURL, username, password);
            return connection;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private int getRowCount(ResultSet resultSet) {
        if (resultSet == null) {
            return 0;
        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        return 0;
    }

    PreparedStatement propagatePropsToQuery(String sqlQuery, Object queryParamObj,Connection connection) throws SQLException, NoSuchFieldException {
        System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"+queryParamObj.getClass().getDeclaredFields().length);
        Pattern pattern = Pattern.compile("(\\$\\{\\w+\\})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);
        StringBuilder builder = new StringBuilder();
        int last = -1;
        //int numberOfProps = 0;
        List<Object> propValues = new ArrayList<>();
        while (matcher.find()) {
            String match = matcher.group(1);
            int len = match.length();
            String propName = match.substring(2, len - 1);

            int start = matcher.start(1);
            int end = matcher.end(1);

            String tmp;
            if (last != -1) {
                // fill from [last+1....start-1]
                assert (last != start);
                tmp = sqlQuery.substring(last + 1, start);
            } else {
                // fill from [0...start-1]
                tmp = sqlQuery.substring(0, start);
            }
            System.out.println(tmp);
            builder.append(tmp);
            last = end;
            // append the value

            Pair data = Utils.generic.getAttribute(propName, queryParamObj);
            assert data != null;
            Object value = ((Pair<?, ?>) data).getValue();
            System.out.println(value.getClass().toGenericString());
            System.out.println(value.getClass().isArray());

            if (value instanceof List<?>) {
                System.out.println("eeeeeeeeeeeeeeeeee");
                List<?> vals = (List<?>) value;
                int numberOfVals=vals.size();

                if (numberOfVals == 0) {
                    builder.append(" (1=2) ");
                } else {
                    builder.append(" (");
                    for(int indx=0;indx<numberOfVals;indx++){
                        Object val=vals.get(indx);
                        System.out.println("->>>>>>>> " + '\'' + val.toString() + '\'');

                        propValues.add(val.toString());
                        builder.append(" ? ");
                        if(indx+1!=numberOfVals){
                            builder.append(" , ");
                        }
                    }
                    builder.append(") ");
                }
            } else {
                // add ?
                builder.append(" ? ");
                propValues.add(value.toString());
                System.out.println(value);
            }
            System.out.println(start + " xxx " + end);
            System.out.println(builder.toString());
            System.out.println("--------------");
        }

        System.out.println("Final Query::: " + builder.toString());
        PreparedStatement statement = connection.prepareStatement(builder.toString(),ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

        for (int i = 0; i < propValues.size(); i++) {
            System.out.println("Prop::: " + i + " ::: " + propValues.get(i));

            statement.setObject(i + 1, propValues.get(i));
        }
        System.out.println(statement);
        return statement;
    }

    PreparedStatement propagatePropsToQuery2(String sqlQuery, Object queryParamObj,Connection connection) throws SQLException, NoSuchFieldException {
        String prefix = "";
        Pattern pattern = Pattern.compile("(\\$\\{\\w+\\})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);
        StringBuilder builder = new StringBuilder();
        int last = -1;
        //int numberOfProps = 0;
        List<Object> propValues = new ArrayList<>();
        while (matcher.find()) {
            String match = matcher.group(1);
            int len = match.length();
            String propName = match.substring(2, len - 1);

            int start = matcher.start(1);
            int end = matcher.end(1);

            String tmp;
            if (last != -1) {
                // fill from [last+1....start-1]
                assert (last != start);
                tmp = sqlQuery.substring(last + 1, start);
            } else {
                // fill from [0...start-1]
                tmp = sqlQuery.substring(0, start);
            }
            System.out.println(tmp);
            builder.append(tmp);
            last = end;
            // append the value

            Pair data = Utils.generic.getAttribute(propName, queryParamObj);
            assert data != null;
            Object value = ((Pair<?, ?>) data).getValue();
            System.out.println(value.getClass().toGenericString());
            System.out.println(value.getClass().isArray());

            // add ?
            builder.append(prefix);
            prefix=",";
            builder.append(" ? ");
            propValues.add(value);

            System.out.println(start + " xxx " + end);
            System.out.println(builder.toString());
            System.out.println("--------------");
        }
        builder.append(" ) ");
        System.out.println("Final Query::: " + builder.toString());
        PreparedStatement statement = connection.prepareStatement(builder.toString(),ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

        for (int i = 0; i < propValues.size(); i++) {
            System.out.println("Prop::: " + i + " ::: " + propValues.get(i));

            statement.setObject(i + 1, propValues.get(i));
        }
        return statement;
    }

    @Override
    public Object selectOne(String queryId, Object queryParam, Class resultType) {
        // Creating Connection
        System.out.println(Arrays.toString(queryParam.getClass().getDeclaredFields()));
        Connection con;
        con = Connection();
        System.out.println(queryParam.getClass().getTypeName());
        try{
            // Based on the XML information creating the class ResultType
            Class<?> clazz ;
            clazz = (resultType);
            if (clazz == null) {
                System.out.println("class not found. Go eat some waffles and correct the name");
                return null;
            }

            // Creating Object of the Class ResultType
            Object o=clazz.newInstance();

            // Create an empty hash map by declaring object
            // of string and integer type
            HashMap<String, Integer> map = new HashMap<>();
//          Getting the fields of the Object o and adding them to HashMap
            System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"+queryParam.getClass().getDeclaredFields().length);
            for (Field field : queryParam.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                System.out.printf("%s", name);
            }
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                map.put(name,1);
                Object value = field.get(o);
                System.out.println("Sdssssssssssssss");
                System.out.printf("%s: %s%n", name, value);
            }
            String[] arr;
            // Here I am getting Query and Class Name from Class Attempt(XML parsing Class)
            arr=attempt.find(queryId);
            // Performing the Query
            assert arr != null;
            PreparedStatement stat =propagatePropsToQuery(arr[0],queryParam,con);
            //PreparedStatement stat=con.prepareStatement("select PersonID,LastName from Classes.Persons where PersonID =?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet;
            resultSet=stat.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount=getRowCount(resultSet);
            System.out.println(rowCount);
            if(rowCount==0)
                return null;
            int code;
            String title;
//            while (resultSet.next()) {
//                code = resultSet.getInt("PersonID");
//                title = resultSet.getString("LastName").trim();
//                System.out.println("Code : " + code
//                        + " Title : " + title);
//            }

            if(rowCount>1)
                throw new RuntimeException("Multiple Rows Selected");

            HashMap<String, Integer> map2 = new HashMap<>();
            // The column count starts from 1
            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                // Do stuff with name
                // Checking if a key is present
                if (!map.containsKey(name)) {
                    throw new RuntimeException("Column Name Issue");
                }
                map2.put(name,1);
                System.out.println(name);
            }
            System.out.println("ggg");
            // Set values to the rows of ResultType Object
            // Setting the fields in the object o
            resultSet.first();
            int i=1;
            for(Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                if(map2.containsKey(name)) {
                    int type = rsmd.getColumnType(i);
                    i++;
                    if (type == Types.VARCHAR || type == Types.CHAR) {
                        System.out.println(resultSet.getString(name));
                        field.set(o, resultSet.getString(name));
                    }
                    if (type == Types.INTEGER) {
                        field.set(o, resultSet.getInt(name));
                        System.out.println(resultSet.getInt(name));
                    }
                    if (type == Types.TIMESTAMP) {
                        field.set(o, resultSet.getTimestamp(name));
                        //System.out.println(resultSet.getInt(name));
                    }
                }
            }
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(o);
                System.out.printf("%s: %s%n", name, value);
            }
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
        con = Connection();
        try {
            // Based on the XML information creating the class "org.bar.foo"
            Class<?> clazz = null;
            clazz = (resultItemType);
            if (clazz == null) {
                System.out.println("class not found. Go eat some waffles and correct the name");
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
                Object value = field.get(o);
                System.out.printf("%s: %s%n", name, value);
            }
            String[] arr;
            // Here I am getting Query and Class Name from Class Attempt(XML parsing Class)
            arr=attempt.find(queryId);
            assert arr != null;
            System.out.println("weweweweewewweweweeweeweweweweweweweweweweweewewew");
            PreparedStatement stat =propagatePropsToQuery(arr[0],queryParam,con);
            //PreparedStatement stat=con.prepareStatement("select PersonID,LastName from Classes.Persons where PersonID =?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet,rs;
            resultSet=stat.executeQuery();
            rs=resultSet;
            int code;
            String title;

            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount = getRowCount(resultSet);
            if(rowCount==0)
                return null;
            System.out.println(columnCount+rowCount);
            System.out.println("weweweweewewweweweeweeweweweweweweweweweweweewewew");
            HashMap<String, Integer> map2 = new HashMap<>();
            List<Object> final_Result = new ArrayList<>();
            // The column count starts from 1
            for (int i = 1; i <= columnCount; i++) {
                String name = rsmd.getColumnName(i);
                // Do stuff with name
                // Checking if a key is present
                if (!map.containsKey(name)) {
                    throw new RuntimeException("Column Name Issue");
                }
                map2.put(name, 1);
                System.out.println(name);
            }

            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+map.size());
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
                        //System.out.println(type);
                        if (type == Types.VARCHAR || type == Types.CHAR) {
                            field.set(final_Result.get(j), rs.getString(name));
                        }
                        if (type == Types.INTEGER) {
                            field.set(final_Result.get(j), rs.getInt(name));
                        }
                        if (type == Types.TIMESTAMP) {
                            field.set(o, resultSet.getTimestamp(name));
                            //System.out.println(resultSet.getInt(name));
                        }
                        i++;
                    }
                }
                //final_Result.add(o);
                j++;
            }while(rs.next());

            System.out.println("------------------");
            for (int jj = 0; jj < rowCount; jj++){
                for (Field field : final_Result.get(jj).getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    String name = field.getName();
                    Object value = field.get(final_Result.get(jj));
                    System.out.printf("%s: %s%n", name, value);
                }
            }
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
        con = Connection();
        // Getting XML information
        try{
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String[] arr;
            // Here I am getting Query and Class Name from Class Attempt(XML parsing Class)
            arr=attempt.find(queryId);
            assert arr != null;
            if(arr[0]==null)
                throw new RuntimeException("Wrong QueryID");

            PreparedStatement stat =propagatePropsToQuery(arr[0],queryParam,con);
            int count = stat.executeUpdate();
            System.out.println("The number of rows affected are:"+ count);
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
        con = Connection();
        // Getting XML information
        try{
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String[] arr;
            arr=attempt.find(queryId);
            assert arr != null;
            if(arr[0]==null)
                throw new RuntimeException("Wrong QueryID");

            // Based on the XML information creating the class QueryParam/org.foo.bar
            Class<?> clazz = null;
            try {
                clazz = Class.forName(arr[1]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz == null) {
                System.out.println("class not found. Go eat some waffles and correct the name");
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
                System.out.println(field.getName());
            }
//          Getting the fields of the Object o
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(o);
                System.out.printf("%s: %s%n", name, value);
            }
            PreparedStatement stat =propagatePropsToQuery2(arr[0],queryParam,con);
            int count = stat.executeUpdate();
            System.out.println("The number of rows affected are:"+ count);
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
        con = Connection();

        // Getting XML information
        try{
            Statement statement;
            statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String[] arr;
            arr=attempt.find(queryId);
            assert arr != null;
            if(arr[0]==null)
                throw new RuntimeException("Wrong QueryID");
            PreparedStatement stat =propagatePropsToQuery(arr[0],queryParam,con);
            //String query=" UPDATE Classes.Persons SET LastName=\"Perez\"  WHERE PersonID=1;";
            int count = stat.executeUpdate();
            System.out.println("The number of rows affected are:"+ count);
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

// Todo :XML and queryParam Class Matching plus cleanup of code


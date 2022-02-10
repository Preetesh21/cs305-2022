package Utils;

import javafx.util.Pair;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

public class helper {

    private static final Map<String, Class<?>> WRAPPER_TYPE_MAP;
    static {
        WRAPPER_TYPE_MAP = new HashMap<>(16);
        WRAPPER_TYPE_MAP.put(Integer.class.getName(), int.class);
        WRAPPER_TYPE_MAP.put(Byte.class.getName(), byte.class);
        WRAPPER_TYPE_MAP.put(Character.class.getName(), char.class);
        WRAPPER_TYPE_MAP.put(Boolean.class.getName(), boolean.class);
        WRAPPER_TYPE_MAP.put(Double.class.getName(), double.class);
        WRAPPER_TYPE_MAP.put(Float.class.getName(), float.class);
        WRAPPER_TYPE_MAP.put(Long.class.getName(), long.class);
        WRAPPER_TYPE_MAP.put(Short.class.getName(), short.class);
        WRAPPER_TYPE_MAP.put(Void.class.getName(), void.class);
        WRAPPER_TYPE_MAP.put(String.class.getName(), String.class);
        WRAPPER_TYPE_MAP.put(ArrayList.class.getName(), ArrayList.class);
        WRAPPER_TYPE_MAP.put(Timestamp.class.getName(), Timestamp.class);
    }
    public static boolean isPrimitiveType(Object source) {
        return WRAPPER_TYPE_MAP.containsKey(source.getClass().getName());
    }
    public static Pair<Object, Object> getAttribute(String attributeName, Object obj) throws NoSuchFieldException, IllegalAccessException {

            Class<?> classInstance = obj.getClass();
            if(isPrimitiveType(obj) ) {
                return new Pair<>(attributeName, obj);
            }
            if(obj instanceof int[] ) {
                List<Long> ttt = new ArrayList<Long>();
                String arr = Arrays.toString((int[]) obj);
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                for (String item : items) {
                    ttt.add(Long.parseLong(item));
                }
                return new Pair<>(attributeName, ttt);
            }

            if(obj instanceof String[]) {
                List<String> ttt = new ArrayList<String>();
                String arr = Arrays.toString((String[]) obj);
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                for (String item : items) {
                    ttt.add((item));
                }
                return new Pair<>(attributeName, ttt);
            }

            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                //System.out.println(name==attributeName);
                if (name.equals(attributeName)) {
                    Object value=field.get(obj);
                    return new Pair<>(field, value);
                }
            }
        return null;
    }
}
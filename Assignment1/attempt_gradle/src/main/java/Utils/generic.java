package Utils;

import javafx.util.Pair;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class generic {

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
    public static Pair<Object, Object> getAttribute(String attributeName, Object obj) throws NoSuchFieldException {
        try {
            Class<?> classInstance = obj.getClass();
            System.out.println(obj.getClass().getName()+Integer.class.getName());
            if(isPrimitiveType(obj) ) {
                System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
                return new Pair<>(attributeName, obj);
            }
            if(obj instanceof int[] ) {
                List<Long> ttt = new ArrayList<Long>();
                String arr = java.util.Arrays.toString((int[]) obj);
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                for (String item : items) {
                    try {
                        System.out.println(item);
                        //assert false;
                        ttt.add(Long.parseLong(item));
                    } catch (NumberFormatException nfe) {
                        //NOTE: write something here if you need to recover from formatting errors
                    }
                    ;
                }
                return new Pair<>(attributeName, ttt);
            }
            if(obj instanceof String[]) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                List<String> ttt = new ArrayList<String>();
                String arr = java.util.Arrays.toString((String[]) obj);
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                for (String item : items) {
                    try {
                        System.out.println(item);
                        //assert false;
                        ttt.add((item));
                    } catch (NumberFormatException nfe) {
                        //NOTE: write something here if you need to recover from formatting errors
                    }
                }
                return new Pair<>(attributeName, ttt);
            }
            if(obj instanceof boolean[]) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                List<Boolean> ttt = new ArrayList<Boolean>();
                String arr = java.util.Arrays.toString((Boolean[]) obj);
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                for (String item : items) {
                    try {
                        System.out.println(item);
                        //assert false;
                        ttt.add(Boolean.parseBoolean(item));
                    } catch (NumberFormatException nfe) {
                        //NOTE: write something here if you need to recover from formatting errors
                    }
                }
                return new Pair<>(attributeName, ttt);
            }
            if(obj instanceof float[] || obj instanceof double[]) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                List<Float> ttt = new ArrayList<Float>();
                String arr = java.util.Arrays.toString((Float[]) obj);
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                for (String item : items) {
                    try {
                        System.out.println(item);
                        //assert false;
                        ttt.add(Float.parseFloat(item));
                    } catch (NumberFormatException nfe) {
                        //NOTE: write something here if you need to recover from formatting errors
                    }
                }
                return new Pair<>(attributeName, ttt);
            }
            if(obj instanceof Timestamp[]) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                List<Timestamp> ttt = new ArrayList<Timestamp>();
                String arr = java.util.Arrays.toString((Timestamp[]) obj);
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                for (String item : items) {
                    try {
                        System.out.println(item);
                        //assert false;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS");
                        Date dateTime = (Date) formatter.parse(item);
                        Timestamp timeStampDate = new Timestamp(dateTime.getTime());
                        ttt.add(timeStampDate);
                    } catch (NumberFormatException nfe) {
                        //NOTE: write something here if you need to recover from formatting errors
                    }
                }
                return new Pair<>(attributeName, ttt);
            }
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                //System.out.println(name==attributeName);
                if (name.equals(attributeName)) {
                    System.out.println("Ssssssssssssssssssssssssssssssssss"+name);
                    Object value=field.get(obj);
                    return new Pair<>(field, value);
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException while fetching: " + attributeName);
        }
        return null;
    }
}
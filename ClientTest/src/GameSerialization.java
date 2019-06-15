import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameSerialization {

    public final static Set<Class> acceptedTypes = new HashSet<>(
            Arrays.asList(byte.class, Byte.class, short.class, Short.class, char.class, Character.class, int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class, String.class)
    );

    public static byte[] serialize(Object o) throws IllegalAccessException, UnsupportedEncodingException {
        Field[] fields = o.getClass().getFields();

        int mem = 0;

        for (Field field : fields) {
            Class type = field.getType();
            if (!acceptedTypes.contains(type)) continue;
            if (type != String.class) {
                mem += sizeOf(type);
            } else {
                String val = (String) field.get(o);
                byte[] bytes = val.getBytes();
                mem += bytes.length;
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(mem);

        for (Field field : fields) {
            Class type = field.getType();
            if (!acceptedTypes.contains(type)) continue;
            if (type == byte.class || type == Byte.class) {
                buffer.put(field.getByte(o));
            }
            else if (type == short.class || type == Short.class) {
                buffer.putShort(field.getShort(o));
            }
            else if (type == char.class || type == Character.class) {
                buffer.putChar(field.getChar(o));
            }
            else if (type == int.class || type == Integer.class) {
                buffer.putInt(field.getInt(o));
            }
            else if (type == long.class || type == Long.class) {
                buffer.putLong(field.getLong(o));
            }
            else if (type == float.class || type == Float.class) {
                buffer.putFloat(field.getFloat(o));
            }
            else if (type == double.class || type == Double.class) {
                buffer.putDouble(field.getDouble(o));
            }
            else if (type == String.class) {
                String val = (String) field.get(o);
                byte[] bytes = val.getBytes("UTF-8");
                buffer.putInt(bytes.length);
                buffer.put(bytes);
            }
        }

        return buffer.array();
    }

    public static Object deserialize(byte[] data, Object o) throws IllegalAccessException{
        ByteBuffer buffer = ByteBuffer.wrap(data);

        Field[] fields = o.getClass().getFields();
        for (Field field : fields) {
            Class type = field.getType();
            if (!acceptedTypes.contains(type)) continue;
            if (type == byte.class || type == Byte.class) {
                field.setByte(o, buffer.get());
            }
            else if (type == short.class || type == Short.class) {
                field.setByte(o, buffer.get());
            }
            else if (type == char.class || type == Character.class) {
                field.setByte(o, buffer.get());
            }
            else if (type == int.class || type == Integer.class) {
                field.setByte(o, buffer.get());
            }
            else if (type == long.class || type == Long.class) {
                field.setByte(o, buffer.get());
            }
            else if (type == float.class || type == Float.class) {
                field.setByte(o, buffer.get());
            }
            else if (type == double.class || type == Double.class) {
                field.setByte(o, buffer.get());
            }
            else if (type == String.class) {
                int byteCount = buffer.getInt();
                byte[] bytes = new byte[byteCount];
                buffer.get(bytes);
                new String(bytes, "UTF-8");
            }
        }
    }

    public static int sizeOf(Class dataType) {
        if (dataType == null) {
            throw new NullPointerException();
        }
        if (dataType == byte.class || dataType == Byte.class) {
            return Byte.SIZE;
        }
        if (dataType == short.class || dataType == Short.class) {
            return Short.SIZE;
        }
        if (dataType == char.class || dataType == Character.class) {
            return Character.SIZE;
        }
        if (dataType == int.class || dataType == Integer.class) {
            return Integer.SIZE;
        }
        if (dataType == long.class || dataType == Long.class) {
            return Long.SIZE;
        }
        if (dataType == float.class || dataType == Float.class) {
            return Float.SIZE;
        }
        if (dataType == double.class || dataType == Double.class) {
            return Double.SIZE;
        }
        return 8; // default for 32-bit memory pointer
    }
}

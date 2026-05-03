package util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionInspector {
    public static String inspect(Object obj) {
        var sb = new StringBuilder();
        Class<?> clazz = obj.getClass();

        sb.append("Class: ").append(clazz.getSimpleName()).append("\n");

        if (clazz.getSuperclass() != null) {
            sb.append("Superclass: ").append(clazz.getSuperclass().getSimpleName()).append("\n");
        }

        sb.append("Methods:\n");
        for (Method m : clazz.getDeclaredMethods()) {
            sb.append("  ").append(m.getName())
              .append("(").append(m.getParameterCount()).append(" params)")
              .append(" -> ").append(m.getReturnType().getSimpleName())
              .append("\n");
        }

        sb.append("Interfaces:\n");
        for (Class<?> iface : clazz.getInterfaces()) {
            sb.append("  ").append(iface.getSimpleName()).append("\n");
        }

        if (clazz.isSealed()) {
            sb.append("Permitted subclasses:\n");
            for (Class<?> sub : clazz.getPermittedSubclasses()) {
                sb.append("  ").append(sub.getSimpleName()).append("\n");
            }
        }

        return sb.toString();
    }

    public static String inspectFields(Object obj) {
        var sb = new StringBuilder();
        Class<?> clazz = obj.getClass();

        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                sb.append(f.getName()).append(" = ").append(f.get(obj)).append("\n");
            } catch (IllegalAccessException e) {
                sb.append(f.getName()).append(" = [inaccessible]\n");
            }
        }
        return sb.toString();
    }
}

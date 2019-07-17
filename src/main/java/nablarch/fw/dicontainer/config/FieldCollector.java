package nablarch.fw.dicontainer.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

public final class FieldCollector {

    private final Set<Field> fields = new LinkedHashSet<>();

    public void addInstanceField(final Field field) {
        if (isTarget(field) == false) {
            return;
        }
        fields.add(field);
    }

    private static boolean isTarget(final Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        return true;
    }

    public Set<Field> getFields() {
        return fields;
    }
}

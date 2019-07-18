package nablarch.fw.dicontainer.component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class FieldCollector {

    private final List<Field> fields = new ArrayList<>();

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

    public List<Field> getFields() {
        return fields;
    }
}

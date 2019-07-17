package nablarch.fw.dicontainer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class AliasMapping {

    private final Map<ComponentKey.AliasKey, Set<ComponentKey<?>>> aliasesMap = new LinkedHashMap<>();

    public <T> void register(final ComponentKey.AliasKey aliasKey, final ComponentKey<T> key) {
        if (aliasesMap.containsKey(aliasKey) == false) {
            aliasesMap.put(aliasKey, new LinkedHashSet<>());
        }
        final Set<ComponentKey<?>> keys = aliasesMap.get(aliasKey);
        keys.add(key);
    }

    public <T> ComponentKey<T> find(final ComponentKey.AliasKey aliasKey) {
        final Set<ComponentKey<?>> keys = aliasesMap.get(aliasKey);
        if (keys == null) {
            return null;
        }
        if (keys.size() > 1) {
            //TODO error
            throw new RuntimeException();
        }
        final ComponentKey<T> key = (ComponentKey<T>) keys.iterator().next();
        return key;
    }
}

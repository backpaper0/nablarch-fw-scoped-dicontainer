package nablarch.fw.dicontainer.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nablarch.fw.dicontainer.ComponentKey;

public final class AliasMapping {

    private final Map<ComponentKey.AliasKey, Set<ComponentKey<?>>> aliasesMap = new LinkedHashMap<>();

    public <T> void register(final ComponentKey.AliasKey aliasKey, final ComponentKey<T> key) {
        if (aliasesMap.containsKey(aliasKey) == false) {
            aliasesMap.put(aliasKey, new LinkedHashSet<>());
        }
        final Set<ComponentKey<?>> keys = aliasesMap.get(aliasKey);
        keys.add(key);
    }

    public Set<ComponentKey<?>> find(final ComponentKey.AliasKey aliasKey) {
        final Set<ComponentKey<?>> keys = aliasesMap.get(aliasKey);
        if (keys == null) {
            return Collections.emptySet();
        }
        return keys;
    }
}
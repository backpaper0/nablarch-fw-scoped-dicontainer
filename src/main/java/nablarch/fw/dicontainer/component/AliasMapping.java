package nablarch.fw.dicontainer.component;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * エイリアスキーと検索キーをマッピングするクラス。
 *
 */
public final class AliasMapping {

    /**
     * マッピング
     */
    private final Map<ComponentKey.AliasKey, Set<ComponentKey<?>>> aliasesMap = new LinkedHashMap<>();

    /**
     * エイリアスキーと検索キーをマッピングする。
     * 
     * @param aliasKey エイリアスキー
     * @param key 検索キー
     */
    public void register(final ComponentKey.AliasKey aliasKey, final ComponentKey<?> key) {
        aliasesMap.computeIfAbsent(aliasKey, a -> new HashSet<>()).add(key);
    }

    /**
     * マッピングされた検索キーを取得する。
     * 
     * @param aliasKey エイリアスキー
     * @return 検索キーの集合
     */
    public Set<ComponentKey<?>> find(final ComponentKey.AliasKey aliasKey) {
        return aliasesMap.getOrDefault(aliasKey, Collections.emptySet());
    }
}

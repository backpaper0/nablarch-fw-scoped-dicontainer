package nablarch.fw.dicontainer.component;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DIコンテナからコンポーネントを取得するときに使用される検索キー。
 *
 * @param <T> コンポーネントの型
 */
public final class ComponentKey<T> implements Serializable {

    /**
     * コンポーネントのクラス
     */
    private final Class<T> componentType;
    /**
     * 限定子
     */
    private final Set<Annotation> qualifiers;

    /**
     * インスタンスを生成する。
     * 
     * @param componentType コンポーネントのクラス
     * @param qualifiers 限定子
     */
    public ComponentKey(final Class<T> componentType, final Annotation... qualifiers) {
        this(componentType, Arrays.stream(qualifiers).collect(Collectors.toSet()));
    }

    /**
     * インスタンスを生成する。
     * 
     * @param componentType コンポーネントのクラス
     * @param qualifiers 限定子
     */
    public ComponentKey(final Class<T> componentType, final Set<Annotation> qualifiers) {
        this.componentType = Objects.requireNonNull(componentType);
        this.qualifiers = Objects.requireNonNull(qualifiers);
    }

    /**
     * この検索キーから導出できるエイリアスキーの集合を返す。
     * 
     * <p>導出されるエイリアスキーは次の通り。</p>
     * <ul>
     * <li>同じ限定子を持つスーパークラスや祖先クラス</li>
     * <li>同じ限定子を持つ実装インターフェース</li>
     * <li>限定子を持たない同じクラス</li>
     * <li>限定子を持たないスーパークラスや祖先クラス</li>
     * <li>限定子を持たない実装インターフェース</li>
     * </ul>
     * 
     * @return エイリアスキーの集合
     */
    public Set<AliasKey> aliasKeys() {
        final Set<Class<?>> classes = new HashSet<>();
        collectAlias(classes, componentType);
        final Stream<AliasKey> withQualifier = classes.stream()
                .map(a -> new AliasKey(a, qualifiers));
        final Stream<AliasKey> classOnly = classes.stream()
                .map(a -> new AliasKey(a, Collections.emptySet()));
        final Stream<AliasKey> selfClassOnly;
        if (qualifiers.isEmpty()) {
            selfClassOnly = Stream.empty();
        } else {
            selfClassOnly = Stream.of(new AliasKey(componentType, Collections.emptySet()));
        }
        return Stream.concat(withQualifier, Stream.concat(classOnly, selfClassOnly))
                .collect(Collectors.toSet());
    }

    /**
     * エイリアスキーに変換する。
     * 
     * @return エイリアスキー
     */
    public AliasKey asAliasKey() {
        return new AliasKey(componentType, qualifiers);
    }

    /**
     * コンポーネントの完全修飾クラス名を返す。
     * 
     * @return コンポーネントの完全修飾クラス名
     */
    public String getFullyQualifiedClassName() {
        return componentType.getName();
    }

    private void collectAlias(final Set<Class<?>> classes, final Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        if (clazz != componentType) {
            classes.add(clazz);
        }
        collectAlias(classes, clazz.getSuperclass());
        for (final Class<?> i : clazz.getInterfaces()) {
            collectAlias(classes, i);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentType, qualifiers);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        final ComponentKey<?> other = (ComponentKey<?>) obj;
        return componentType.equals(other.componentType)
                && qualifiers.equals(other.qualifiers);
    }

    @Override
    public String toString() {
        if (qualifiers.isEmpty()) {
            return componentType.getName();
        }
        return qualifiers.stream().map(Objects::toString)
                .collect(Collectors.joining(", ", componentType.getName() + "(", ")"));
    }

    /**
     * エイリアスキー
     *
     */
    public static final class AliasKey {

        /**
         * エイリアスのクラス
         */
        private final Class<?> aliasType;
        /**
         * 限定子
         */
        private final Set<Annotation> qualifiers;

        /**
         * インスタンスを生成する。
         * 
         * @param aliasType エイリアスのクラス
         * @param qualifiers 限定子
         */
        private AliasKey(final Class<?> aliasType, final Set<Annotation> qualifiers) {
            this.aliasType = Objects.requireNonNull(aliasType);
            this.qualifiers = Objects.requireNonNull(qualifiers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aliasType, qualifiers);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (getClass() != obj.getClass()) {
                return false;
            }
            final AliasKey other = (AliasKey) obj;
            return aliasType.equals(other.aliasType)
                    && qualifiers.equals(other.qualifiers);
        }

        @Override
        public String toString() {
            if (qualifiers.isEmpty()) {
                return aliasType.getName();
            }
            return qualifiers.stream().map(Objects::toString)
                    .collect(Collectors.joining(", ", aliasType.getName() + "(", ")"));
        }
    }
}

package nablarch.fw.dicontainer.annotation.auto;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 名前をもとにクラスをフィルタリングするクラス。
 *
 */
public final class ClassFilter {

    /**
     * 含めるクラスのパターン
     */
    private final Patterns includes;
    /**
     * 除外するクラスのパターン
     */
    private final Patterns excludes;

    /**
     * インスタンスを生成する。
     * 
     * @param includes 含めるクラスのパターン
     * @param excludes 除外するクラスのパターン
     */
    private ClassFilter(final Patterns includes, final Patterns excludes) {
        this.includes = Objects.requireNonNull(includes);
        this.excludes = Objects.requireNonNull(excludes);
    }

    /**
     * フィルタリングを行う。
     * 
     * @param className クラスの完全修飾名
     * @return 含める場合は{@literal true}
     */
    public boolean select(final String className) {
        return includes(className) && excludes(className) == false;
    }

    private boolean includes(final String className) {
        if (includes.isEmpty()) {
            return true;
        }
        return includes.matches(className);
    }

    private boolean excludes(final String className) {
        if (excludes.isEmpty()) {
            return false;
        }
        return excludes.matches(className);
    }

    /**
     * トラバーサルの設定をもとにインスタンスを生成する。
     * 
     * @param traversalConfig トラバーサルの設定
     * @return インスタンス
     */
    public static ClassFilter valueOf(final TraversalConfig traversalConfig) {
        final Patterns includes = Patterns.valueOf(traversalConfig.includes());
        final Patterns excludes = Patterns.valueOf(traversalConfig.excludes());
        return new ClassFilter(includes, excludes);
    }

    /**
     * 全てのクラスを含めるフィルタを生成する。
     * 
     * @return インスタンス。
     */
    public static ClassFilter allClasses() {
        final Patterns includes = Patterns.empty();
        final Patterns excludes = Patterns.empty();
        return new ClassFilter(includes, excludes);
    }

    private static final class Patterns {

        private final Set<Pattern> patterns;

        private Patterns(final Set<Pattern> patterns) {
            this.patterns = Objects.requireNonNull(patterns);
        }

        public boolean isEmpty() {
            return patterns.isEmpty();
        }

        public boolean matches(final String className) {
            return patterns.stream().map(pattern -> pattern.matcher(className))
                    .anyMatch(Matcher::matches);
        }

        public static Patterns valueOf(final Set<String> patterns) {
            return new Patterns(
                    patterns.stream().map(Pattern::compile).collect(Collectors.toSet()));
        }

        public static Patterns empty() {
            return new Patterns(Collections.emptySet());
        }
    }
}

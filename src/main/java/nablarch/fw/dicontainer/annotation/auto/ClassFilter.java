package nablarch.fw.dicontainer.annotation.auto;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ClassFilter {

    private final Set<Pattern> includes;
    private final Set<Pattern> excludes;

    public ClassFilter(final Set<Pattern> includes, final Set<Pattern> excludes) {
        this.includes = Objects.requireNonNull(includes);
        this.excludes = Objects.requireNonNull(excludes);
    }

    public boolean select(final String className) {
        return includes(className) && excludes(className) == false;
    }

    private boolean includes(final String className) {
        if (includes.isEmpty()) {
            return true;
        }
        for (final Pattern pattern : includes) {
            final Matcher matcher = pattern.matcher(className);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean excludes(final String className) {
        if (excludes.isEmpty()) {
            return false;
        }
        for (final Pattern pattern : excludes) {
            final Matcher matcher = pattern.matcher(className);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public static ClassFilter valueOf(final TraversalConfig traversalConfig) {
        final Set<Pattern> includes = traversalConfig.includes().stream().map(Pattern::compile)
                .collect(Collectors.toSet());
        final Set<Pattern> excludes = traversalConfig.excludes().stream().map(Pattern::compile)
                .collect(Collectors.toSet());
        return new ClassFilter(includes, excludes);
    }

    public static ClassFilter allClasses() {
        final Set<Pattern> includes = Collections.emptySet();
        final Set<Pattern> excludes = Collections.emptySet();
        return new ClassFilter(includes, excludes);
    }
}

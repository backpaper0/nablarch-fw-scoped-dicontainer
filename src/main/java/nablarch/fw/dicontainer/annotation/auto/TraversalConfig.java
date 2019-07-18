package nablarch.fw.dicontainer.annotation.auto;

import java.util.Collections;
import java.util.Set;

public interface TraversalConfig {

    default Set<String> includes() {
        return Collections.emptySet();
    }

    default Set<String> excludes() {
        return Collections.emptySet();
    }
}

package nablarch.fw.dicontainer.auto;

import java.util.Collections;
import java.util.Set;

public interface TraversalMark {

    default Set<String> includes() {
        return Collections.emptySet();
    }

    default Set<String> excludes() {
        return Collections.emptySet();
    }
}

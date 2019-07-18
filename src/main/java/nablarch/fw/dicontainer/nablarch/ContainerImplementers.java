package nablarch.fw.dicontainer.nablarch;

import java.util.Collections;
import java.util.Map;

import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public final class ContainerImplementers {

    private ContainerImplementers() {
    }

    public static ContainerImplementer get() {
        final ContainerImplementer container = SystemRepository.get(name());
        return container;
    }

    public static void set(final ContainerImplementer container) {
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                return Collections.singletonMap(name(), container);
            }
        });
    }

    public static void clear() {
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                return Collections.singletonMap(name(), null);
            }
        });
    }

    private static String name() {
        return ContainerImplementer.class.getName();
    }
}

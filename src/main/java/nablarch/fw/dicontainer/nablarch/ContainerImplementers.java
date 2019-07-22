package nablarch.fw.dicontainer.nablarch;

import java.util.Collections;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public final class ContainerImplementers {

    private ContainerImplementers() {
    }

    public static ContainerImplementer get() {
        return SystemRepository.get(name());
    }

    public static void set(final ContainerImplementer container) {
        SystemRepository.load(() -> Collections.singletonMap(name(), container));
    }

    public static void clear() {
        SystemRepository.load(() -> Collections.singletonMap(name(), null));
    }

    private static String name() {
        return Container.class.getName();
    }
}

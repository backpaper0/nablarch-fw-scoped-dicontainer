package nablarch.fw.dicontainer.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ContainerException;

public final class ErrorCollector {

    private final List<ContainerException> exceptions = new ArrayList<>();
    private final Set<Class<? extends ContainerException>> ignoreExceptionClasses = new HashSet<>();

    public void add(final ContainerException exception) {
        exceptions.add(exception);
    }

    public void ignore(final Class<? extends ContainerException> ignoreMe) {
        ignoreExceptionClasses.add(ignoreMe);
    }

    public void throwExceptionIfExistsError() {
        final List<ContainerException> filtered = exceptions.stream()
                .filter(a -> ignoreExceptionClasses.contains(a.getClass()) == false)
                .collect(Collectors.toList());
        if (filtered.isEmpty() == false) {
            throw new ContainerCreationException(filtered);
        }
    }
}

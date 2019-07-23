package nablarch.fw.dicontainer.component.impl;

import java.util.Objects;

import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.exception.ContainerException;

public final class ContainerBuilderWrapper implements ErrorCollector {

    private final ContainerBuilder<?> containerBuilder;

    public ContainerBuilderWrapper(final ContainerBuilder<?> containerBuilder) {
        this.containerBuilder = Objects.requireNonNull(containerBuilder);
    }

    @Override
    public void add(final ContainerException exception) {
        containerBuilder.addError(exception);
    }

    @Override
    public void ignore(final Class<? extends ContainerException> ignoreMe) {
        containerBuilder.ignoreError(ignoreMe);
    }

    @Override
    public void throwExceptionIfExistsError() {
        throw new UnsupportedOperationException("throwExceptionIfExistsError");
    }
}

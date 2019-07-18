package nablarch.fw.dicontainer.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ContainerException;

public interface ErrorCollector {

    void add(final ContainerException exception);

    void ignore(final Class<? extends ContainerException> ignoreMe);

    void throwExceptionIfExistsError();

    static ErrorCollector newInstance() {
        return new ErrorCollectorImpl();
    }

    static ErrorCollector wrap(final ContainerBuilder<?> containerBuilder) {
        return null;
    }

    final class ErrorCollectorImpl implements ErrorCollector {

        private final List<ContainerException> exceptions = new ArrayList<>();
        private final Set<Class<? extends ContainerException>> ignoreExceptionClasses = new HashSet<>();

        @Override
        public void add(final ContainerException exception) {
            exceptions.add(exception);
        }

        @Override
        public void ignore(final Class<? extends ContainerException> ignoreMe) {
            ignoreExceptionClasses.add(ignoreMe);
        }

        @Override
        public void throwExceptionIfExistsError() {
            final List<ContainerException> filtered = exceptions.stream()
                    .filter(a -> ignoreExceptionClasses.contains(a.getClass()) == false)
                    .collect(Collectors.toList());
            if (filtered.isEmpty() == false) {
                throw new ContainerCreationException(filtered);
            }
        }
    }

    final class ContainerBuilderWrapper implements ErrorCollector {

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
}

package nablarch.fw.dicontainer.component.impl;

import java.util.Objects;

import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.exception.ContainerException;

/**
 * {@link ContainerBuilder}を使った{@link ErrorCollector}実装クラス。
 * 収集したエラーを{@link ContainerBuilder}に通知する。
 */
public final class ContainerBuilderWrapper implements ErrorCollector {

    /** ラップ対象の{@link ContainerBuilder} */
    private final ContainerBuilder<?> containerBuilder;

    /**
     * コンストラクタ。
     * @param containerBuilder ラップ対象の{@link ContainerBuilder}
     */
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

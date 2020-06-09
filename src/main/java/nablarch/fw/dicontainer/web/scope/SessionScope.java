package nablarch.fw.dicontainer.web.scope;

import java.util.Objects;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.scope.AbstractScope;
import nablarch.fw.dicontainer.web.context.SessionContext;
import nablarch.fw.dicontainer.web.context.SessionContextSupplier;
import nablarch.fw.dicontainer.web.exception.WebContextException;

/**
 * セッションスコープ。
 *
 */
public final class SessionScope extends AbstractScope {

    /**
     * セッションコンテキストを取得するクラス
     */
    private final SessionContextSupplier supplier;

    /**
     * インスタンスを生成する。
     * 
     * @param supplier セッションコンテキストを取得するクラス
     */
    public SessionScope(final SessionContextSupplier supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider) {
        return getSessionContext().getSessionComponent(id, provider);
    }

    private SessionContext getSessionContext() {
        final SessionContext context = supplier.getSessionContext();
        if (context == null) {
            throw new WebContextException("SessionContext is not found.");
        }
        return context;
    }

    @Override
    public int dimensions() {
        return 200;
    }

    /**
     * コンポーネントを削除する。
     *
     * @param id ID
     * @param <T> コンポーネント
     */
    public <T> T removeComponent(final ComponentId id) {
        return getSessionContext().removeSessionComponent(id);
    }
}
package nablarch.fw.dicontainer.nablarch;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.handler.DelegateFactory;

/**
 * コンテナから委譲クラスのインスタンスを取得する{@link DelegateFactory}実装クラス。
 */
public final class ContainerLookupDelegateFactory implements DelegateFactory {

    @Override
    public Object create(final Class<?> clazz) {
        final Container container = ContainerImplementers.get();
        return container.getComponent(clazz);
    }
}

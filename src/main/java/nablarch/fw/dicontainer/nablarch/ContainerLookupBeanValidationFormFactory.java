package nablarch.fw.dicontainer.nablarch;

import nablarch.common.web.validator.BeanValidationFormFactory;
import nablarch.fw.dicontainer.Container;

/**
 * コンテナからFormを取得する{@link BeanValidationFormFactory}実装クラス。
 */
public final class ContainerLookupBeanValidationFormFactory implements BeanValidationFormFactory {

    @Override
    public <T> T create(final Class<T> formClass) {
        final Container container = Containers.get();
        return container.getComponent(formClass);
    }
}

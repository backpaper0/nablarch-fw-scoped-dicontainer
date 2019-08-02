package nablarch.fw.dicontainer.nablarch;

import nablarch.common.web.validator.BeanValidationFormFactory;
import nablarch.fw.dicontainer.Container;

public final class ContainerLookupBeanValidationFormFactory implements BeanValidationFormFactory {

    @Override
    public <T> T create(final Class<T> formClass) {
        final Container container = ContainerImplementers.get();
        return container.getComponent(formClass);
    }
}

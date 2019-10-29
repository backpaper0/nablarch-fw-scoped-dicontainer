package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

public class MockInjectableConstructor implements InjectableConstructor {

    @Override
    public Object inject(final Container container) {
        return null;
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
    }
}

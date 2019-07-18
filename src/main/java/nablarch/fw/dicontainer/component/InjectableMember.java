package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;

public interface InjectableMember {

    Object inject(Container container, Object component);

    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);

    void validateCycleDependency(CycleDependencyValidationContext context);

    static InjectableMember passthrough(final Object instance) {
        return new InjectableMember() {
            @Override
            public Object inject(final Container container, final Object component) {
                return instance;
            }

            @Override
            public void validate(final ContainerBuilder<?> containerBuilder,
                    final ComponentDefinition<?> self) {
            }

            @Override
            public void validateCycleDependency(final CycleDependencyValidationContext context) {
            }
        };
    }
}

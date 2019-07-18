package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public interface InjectableMember {

    Object inject(ContainerImplementer container, Object component);

    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);

    void validateCycleDependency(CycleDependencyValidationContext context);

    static InjectableMember passthrough(final Object instance) {
        return new InjectableMember() {
            @Override
            public Object inject(final ContainerImplementer container, final Object component) {
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

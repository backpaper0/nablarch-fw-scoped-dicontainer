package nablarch.fw.dicontainer.component;

import static org.junit.Assert.*;

import org.junit.Test;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.scope.PrototypeScope;
import nablarch.fw.dicontainer.scope.SingletonScope;

public class ComponentDefinitionTest {

    private final ComponentDefinition<?> cd1 = ComponentDefinition.builder(getClass())
            .injectableConstructor(new MockInjectableMember())
            .scope(new PrototypeScope())
            .build()
            .get();

    private final ComponentDefinition<?> cd2 = ComponentDefinition.builder(getClass())
            .injectableConstructor(new MockInjectableMember())
            .scope(new SingletonScope())
            .build()
            .get();

    @Test
    public void isNarrowScope() throws Exception {
        assertTrue(cd1.isNarrowScope(cd2));
        assertFalse(cd2.isNarrowScope(cd1));
        assertTrue(cd1.isNarrowScope(cd1));
        assertTrue(cd2.isNarrowScope(cd2));
    }

    private static class MockInjectableMember implements InjectableMember {

        @Override
        public Object inject(final ContainerImplementer container, final Object component) {
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
}

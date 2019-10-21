package nablarch.fw.dicontainer.component;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.scope.PrototypeScope;
import nablarch.fw.dicontainer.scope.SingletonScope;

public class ComponentDefinitionTest {

    private final ComponentDefinition<?> cd1 = ComponentDefinition.builder(getClass())
            .injectableConstructor(new MockInjectableConstructor())
            .scope(new PrototypeScope())
            .build()
            .get();

    private final ComponentDefinition<?> cd2 = ComponentDefinition.builder(getClass())
            .injectableConstructor(new MockInjectableConstructor())
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

    private static class MockInjectableConstructor implements InjectableConstructor {

        @Override
        public Object inject(final ContainerImplementer container) {
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

    @Test
    public void testGetId() {
        assertNotNull(cd1.getId());
    }

    @Test
    public void builderGeneratesComponentId() {
        ComponentId generatedId = ComponentDefinition.builder(getClass()).id();
        assertNotNull(generatedId);
    }
}

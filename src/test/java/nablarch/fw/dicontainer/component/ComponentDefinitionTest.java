package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.scope.PrototypeScope;
import nablarch.fw.dicontainer.scope.SingletonScope;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

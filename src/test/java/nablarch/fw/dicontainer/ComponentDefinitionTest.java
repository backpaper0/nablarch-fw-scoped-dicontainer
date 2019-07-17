package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import org.junit.Test;

public class ComponentDefinitionTest {

    private final ComponentDefinition<?> cd1 = ComponentDefinition.builder()
            .injectableConstructor(InjectableMember.errorMock())
            .scope(new PrototypeScope())
            .build();

    private final ComponentDefinition<?> cd2 = ComponentDefinition.builder()
            .injectableConstructor(InjectableMember.errorMock())
            .scope(new SingletonScope())
            .build();

    @Test
    public void isNarrowScope() throws Exception {
        assertTrue(cd1.isNarrowScope(cd2));
        assertFalse(cd2.isNarrowScope(cd1));
        assertTrue(cd1.isNarrowScope(cd1));
        assertTrue(cd2.isNarrowScope(cd2));
    }
}

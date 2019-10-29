package nablarch.fw.dicontainer.container;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.MockInjectableConstructor;
import nablarch.fw.dicontainer.scope.SingletonScope;
import org.junit.Test;

import static org.junit.Assert.*;

public class CycleDependencyValidationContextTest {

    @Test
    public void testComponentDefinitionMultipleAliasKeys() {
        ContainerBuilder builder = AnnotationContainerBuilder.builder().build();
        ComponentDefinition<Bbb> def = ComponentDefinition.builder(Bbb.class)
                .injectableConstructor(new MockInjectableConstructor())
                .scope(new SingletonScope())
                .build()
                .get();
        CycleDependencyValidationContext sut = CycleDependencyValidationContext.newContext(builder, def);
        sut.validateCycleDependency(new ComponentKey<>(Bbb.class));
    }

    private static class Aaa {
    }

    private static class Bbb extends Aaa {
    }
}
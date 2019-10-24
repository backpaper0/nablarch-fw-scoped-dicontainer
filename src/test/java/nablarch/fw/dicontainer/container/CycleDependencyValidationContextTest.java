package nablarch.fw.dicontainer.container;

import nablarch.fw.dicontainer.component.ComponentDefinition;

import static org.junit.Assert.*;

public class CycleDependencyValidationContextTest {

    public static CycleDependencyValidationContext newContext(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> target) {
        return CycleDependencyValidationContext.newContext(containerBuilder, target);
    }
}
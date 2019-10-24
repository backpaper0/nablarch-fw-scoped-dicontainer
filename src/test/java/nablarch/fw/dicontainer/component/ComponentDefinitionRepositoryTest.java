package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationComponentKeyFactory;
import nablarch.fw.dicontainer.component.factory.ComponentKeyFactory;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;
import nablarch.fw.dicontainer.scope.SingletonScope;
import org.junit.Test;

import javax.inject.Singleton;

import static org.junit.Assert.assertSame;

public class ComponentDefinitionRepositoryTest {

    @Test(expected = ComponentNotFoundException.class)
    public void throwExceptionWhenComponentNotFound() {
        ComponentDefinitionRepository sut = new ComponentDefinitionRepository();
        sut.get(ComponentId.generate());
    }

    @Test
    public void testRegister() {
        ComponentDefinitionRepository sut = new ComponentDefinitionRepository();
        ComponentKeyFactory keyFactory = new AnnotationComponentKeyFactory();
        ComponentKey<Aaa> key = keyFactory.fromComponentClass(Aaa.class);
        ComponentDefinition<Aaa> def = ComponentDefinition.builder(Aaa.class)
                .injectableConstructor(injectableConstructor)
                .scope(new SingletonScope())
                .build()
                .get();
        sut.register(key, def);
        ComponentDefinition<Object> got = sut.get(def.getId());
        assertSame(got, def);
    }


    @Singleton
    private static class Aaa {
    }

    private InjectableConstructor injectableConstructor = new InjectableConstructor() {
        @Override
        public Object inject(Container container) {
            return null;
        }

        @Override
        public void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self) {

        }

        @Override
        public void validateCycleDependency(CycleDependencyValidationContext context) {

        }
    };
}
package nablarch.fw.dicontainer.container;

import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.component.AliasMapping;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentDefinition.Builder;
import nablarch.fw.dicontainer.component.ComponentDefinitionRepository;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.InjectableConstructor;
import nablarch.fw.dicontainer.component.impl.DefaultInjectableConstructor;
import nablarch.fw.dicontainer.component.impl.InjectionComponentResolvers;
import nablarch.fw.dicontainer.nablarch.NablarchWebContextHandler;
import nablarch.fw.dicontainer.web.scope.RequestScope;
import org.junit.Test;

import javax.inject.Singleton;

import java.lang.reflect.Constructor;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class DefaultContainerTest {

    @Test
    public void getComponent() {

        final ContainerImplementer container = (ContainerImplementer) AnnotationContainerBuilder.createDefault()
                .register(Aaa.class)
                .build();

        ComponentKey<Aaa> key = new ComponentKey<>(Aaa.class);
        final Aaa component = container.getComponent(key);
        assertNotNull(component);
    }

    @Test
    public void testComponentId() throws NoSuchMethodException {
        Builder<Aaa> builder = ComponentDefinition.builder(Aaa.class);
        Constructor<Aaa> constructor = Aaa.class.getConstructor();
        builder.injectableConstructor(new DefaultInjectableConstructor(constructor, InjectionComponentResolvers.empty()));
        NablarchWebContextHandler supplier = new NablarchWebContextHandler();
        builder.scope(new RequestScope(supplier));

        ComponentDefinitionRepository definitions = new ComponentDefinitionRepository();
        ComponentDefinition<Aaa> definition = builder.build().get();
        definitions.register(new ComponentKey<>(Aaa.class ), definition);

        DefaultContainer container = new DefaultContainer(definitions, new AliasMapping());
        supplier.handle(null, new ExecutionContext().addHandler((data, context) -> {
            ComponentId id = builder.id();
            Aaa component = container.getComponent(id);
            assertNotNull(component);

            ComponentDefinition<Object> actual = container.getComponentDefinition(id);
            assertSame(definition, actual);
            return null;
        }));
    }

    @Singleton
    private static class Aaa {
        public Aaa() {
        }
    }
}
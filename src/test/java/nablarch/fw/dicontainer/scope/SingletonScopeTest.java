package nablarch.fw.dicontainer.scope;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentDefinition.Builder;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.MockInjectableConstructor;
import nablarch.fw.dicontainer.event.ContainerDestroy;
import org.junit.Test;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SingletonScopeTest {

    private final SingletonScope scope = new SingletonScope();

    @Test
    public void getComponent() throws Exception {
        final ComponentId id = ComponentId.generate();
        final Provider<Aaa> provider = Aaa::new;
        final Aaa component1 = scope.getComponent(id, provider);
        final Aaa component2 = scope.getComponent(id, provider);
        assertTrue(component1 == component2);
    }

    @Test
    public void getComponentMultiThread() throws Exception {
        final ComponentId id = ComponentId.generate();
        final Provider<Bbb> provider = Bbb::new;
        final Bbb[] components = new Bbb[2];
        final Thread t1 = new Thread(() -> {
            components[0] = scope.getComponent(id, provider);
        });
        final Thread t2 = new Thread(() -> {
            components[1] = scope.getComponent(id, provider);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertNotNull(components[0]);
        assertTrue(components[0] == components[1]);
    }



    /**
     * コンポーネントがnullの場合、{@link ComponentDefinition#destroyComponent(Object)}
     * の呼び出しがスキップされること。
     */
    @Test
    public void testDestroySkip() {
        Builder<Aaa> builder = ComponentDefinition.builder(Aaa.class);
        ComponentId id = builder.id();
        ComponentDefinition<Aaa> def = builder.injectableConstructor(new MockInjectableConstructor())
                .scope(new SingletonScope())
                .build()
                .get();
        scope.register(def);
        final Aaa component1 = scope.getComponent(id, () -> null);
        assertNull(component1);

        scope.destroy(new ContainerDestroy());
    }

    static class Aaa {
    }

    static class Bbb {
        public Bbb() {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (final InterruptedException e) {
                throw new AssertionError();
            }
        }
    }

    @Singleton
    static class Ccc {

    }
}

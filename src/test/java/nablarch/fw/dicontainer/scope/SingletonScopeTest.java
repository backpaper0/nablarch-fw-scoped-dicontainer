package nablarch.fw.dicontainer.scope;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import org.junit.Test;

import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.scope.SingletonScope;

public class SingletonScopeTest {

    private final SingletonScope scope = new SingletonScope();

    @Test
    public void getComponent() throws Exception {
        final ComponentId id = ComponentId.generate();
        final Provider<Aaa> provider = Aaa::new;
        final Aaa component1 = scope.getComponent(id, provider, DestroyMethod.noop());
        final Aaa component2 = scope.getComponent(id, provider, DestroyMethod.noop());
        assertTrue(component1 == component2);
    }

    @Test
    public void getComponentMultiThread() throws Exception {
        final ComponentId id = ComponentId.generate();
        final Provider<Bbb> provider = Bbb::new;
        final Bbb[] components = new Bbb[2];
        final Thread t1 = new Thread(() -> {
            components[0] = scope.getComponent(id, provider, DestroyMethod.noop());
        });
        final Thread t2 = new Thread(() -> {
            components[1] = scope.getComponent(id, provider, DestroyMethod.noop());
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertNotNull(components[0]);
        assertTrue(components[0] == components[1]);
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
}

package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import org.junit.Test;

public class SingletonScopeTest {

    private final SingletonScope scope = new SingletonScope();

    @Test
    public void getComponent() throws Exception {
        final ComponentKey<Aaa> key = ComponentKey.fromClass(Aaa.class);
        final Provider<Aaa> provider = Aaa::new;
        final Aaa component1 = scope.getComponent(key, provider, Collections.emptySet());
        final Aaa component2 = scope.getComponent(key, provider, Collections.emptySet());
        assertTrue(component1 == component2);
    }

    @Test
    public void getComponentMultiThread() throws Exception {
        final ComponentKey<Bbb> key = ComponentKey.fromClass(Bbb.class);
        final Provider<Bbb> provider = Bbb::new;
        final Bbb[] components = new Bbb[2];
        final Thread t1 = new Thread(() -> {
            components[0] = scope.getComponent(key, provider, Collections.emptySet());
        });
        final Thread t2 = new Thread(() -> {
            components[1] = scope.getComponent(key, provider, Collections.emptySet());
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

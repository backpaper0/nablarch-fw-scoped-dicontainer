package nablarch.fw.dicontainer.component;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import nablarch.fw.dicontainer.component.MethodCollector;
import nablarch.fw.dicontainer.overridedemo.OverrideDemo1;
import nablarch.fw.dicontainer.overridedemo.OverrideDemo2;
import nablarch.fw.dicontainer.overridedemo.subpackage.OverrideDemo3;

public class MethodCollectorTest {

    @Test
    public void addInstanceMethodIfNotOverridden() throws Exception {
        final MethodCollector collector = new MethodCollector();

        final Method demo1Method1 = OverrideDemo1.class.getDeclaredMethod("method1");
        final Method demo1Method2 = OverrideDemo1.class.getDeclaredMethod("method2");
        final Method demo1Method3 = OverrideDemo1.class.getDeclaredMethod("method3");
        final Method demo1Method4 = OverrideDemo1.class.getDeclaredMethod("method4");

        final Method demo2Method1 = OverrideDemo2.class.getDeclaredMethod("method1");
        final Method demo2Method2 = OverrideDemo2.class.getDeclaredMethod("method2");
        final Method demo2Method3 = OverrideDemo2.class.getDeclaredMethod("method3");
        final Method demo2Method4 = OverrideDemo2.class.getDeclaredMethod("method4");

        collector.addInstanceMethodIfNotOverridden(demo2Method1);
        collector.addInstanceMethodIfNotOverridden(demo2Method2);
        collector.addInstanceMethodIfNotOverridden(demo2Method3);
        collector.addInstanceMethodIfNotOverridden(demo2Method4);

        collector.addInstanceMethodIfNotOverridden(demo1Method1);
        collector.addInstanceMethodIfNotOverridden(demo1Method2);
        collector.addInstanceMethodIfNotOverridden(demo1Method3);
        collector.addInstanceMethodIfNotOverridden(demo1Method4);

        final Set<Method> methods = collector.getMethods();

        final Set<Method> expected = Stream.of(
                demo2Method1,
                demo2Method2,
                demo2Method3,
                demo2Method4,
                demo1Method4).collect(Collectors.toSet());

        assertEquals(expected, methods);
    }

    @Test
    public void addInstanceMethodIfNotOverriddenAnotherPackage() throws Exception {
        final MethodCollector collector = new MethodCollector();

        final Method demo1Method1 = OverrideDemo1.class.getDeclaredMethod("method1");
        final Method demo1Method2 = OverrideDemo1.class.getDeclaredMethod("method2");
        final Method demo1Method3 = OverrideDemo1.class.getDeclaredMethod("method3");
        final Method demo1Method4 = OverrideDemo1.class.getDeclaredMethod("method4");

        final Method demo3Method1 = OverrideDemo3.class.getDeclaredMethod("method1");
        final Method demo3Method2 = OverrideDemo3.class.getDeclaredMethod("method2");
        final Method demo3Method3 = OverrideDemo3.class.getDeclaredMethod("method3");
        final Method demo3Method4 = OverrideDemo3.class.getDeclaredMethod("method4");

        collector.addInstanceMethodIfNotOverridden(demo3Method1);
        collector.addInstanceMethodIfNotOverridden(demo3Method2);
        collector.addInstanceMethodIfNotOverridden(demo3Method3);
        collector.addInstanceMethodIfNotOverridden(demo3Method4);

        collector.addInstanceMethodIfNotOverridden(demo1Method1);
        collector.addInstanceMethodIfNotOverridden(demo1Method2);
        collector.addInstanceMethodIfNotOverridden(demo1Method3);
        collector.addInstanceMethodIfNotOverridden(demo1Method4);

        final Set<Method> methods = collector.getMethods();

        final Set<Method> expected = Stream.of(
                demo3Method1,
                demo3Method2,
                demo3Method3,
                demo3Method4,
                demo1Method3,
                demo1Method4).collect(Collectors.toSet());

        assertEquals(expected, methods);
    }

    @Test
    public void ignoreStaticMethod() throws Exception {
        final MethodCollector collector = new MethodCollector();
        collector.addInstanceMethodIfNotOverridden(Aaa.class.getDeclaredMethod("staticMethod"));

        assertEquals(0, collector.getMethods().size());
    }

    @Test
    public void ignoreBridgeMethod() throws Exception {
        final MethodCollector collector = new MethodCollector();
        for (final Method method : Bbb.class.getDeclaredMethods()) {
            collector.addInstanceMethodIfNotOverridden(method);
        }

        assertEquals(1, collector.getMethods().size());

        final Method method = collector.getMethods().iterator().next();
        assertEquals(Timestamp.class, method.getReturnType());
        assertFalse(method.isBridge());
    }

    @Test
    public void ignoreSyntheticMethod() throws Exception {
        new Ccc().syntheticMethod();
        final MethodCollector collector = new MethodCollector();
        for (final Method method : Ccc.class.getDeclaredMethods()) {
            collector.addInstanceMethodIfNotOverridden(method);
        }

        assertEquals(1, collector.getMethods().size());

        final Method method = collector.getMethods().iterator().next();
        assertEquals(Ccc.class.getDeclaredMethod("syntheticMethod"), method);
        assertFalse(method.isSynthetic());
    }

    @Test
    public void ignoreAbstractMethod() throws Exception {
        final MethodCollector collector = new MethodCollector();
        for (final Method method : Ddd.class.getDeclaredMethods()) {
            collector.addInstanceMethodIfNotOverridden(method);
        }
        for (final Method method : Eee.class.getDeclaredMethods()) {
            collector.addInstanceMethodIfNotOverridden(method);
        }

        assertEquals(0, collector.getMethods().size());
    }

    static class Aaa {
        static void staticMethod() {
        }

        Date bridgeMethod() {
            return null;
        }
    }

    static class Bbb extends Aaa {
        @Override
        Timestamp bridgeMethod() {
            return null;
        }
    }

    static class Ccc {
        private void syntheticMethod() {
        }
    }

    static abstract class Ddd {
        abstract void abstractMethod();
    }

    interface Eee {
        void abstractMethod();
    }
}

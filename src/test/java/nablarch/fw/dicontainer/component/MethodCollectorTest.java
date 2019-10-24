package nablarch.fw.dicontainer.component;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import nablarch.fw.dicontainer.overridedemo.OverrideDemo1;
import nablarch.fw.dicontainer.overridedemo.OverrideDemo2;
import nablarch.fw.dicontainer.overridedemo.subpackage.OverrideDemo3;

public class MethodCollectorTest {

    @Test
    public void addMethodIfNotOverridden() throws Exception {
        final MethodCollector collector = new MethodCollector();

        final Method demo1Method1 = OverrideDemo1.class.getDeclaredMethod("method1");
        final Method demo1Method2 = OverrideDemo1.class.getDeclaredMethod("method2");
        final Method demo1Method3 = OverrideDemo1.class.getDeclaredMethod("method3");
        final Method demo1Method4 = OverrideDemo1.class.getDeclaredMethod("method4");
        final Method demo1Method5 = OverrideDemo1.class.getDeclaredMethod("method5");

        final Method demo2Method1 = OverrideDemo2.class.getDeclaredMethod("method1");
        final Method demo2Method2 = OverrideDemo2.class.getDeclaredMethod("method2");
        final Method demo2Method3 = OverrideDemo2.class.getDeclaredMethod("method3");
        final Method demo2Method4 = OverrideDemo2.class.getDeclaredMethod("method4");
        final Method demo2Method5 = OverrideDemo2.class.getDeclaredMethod("method5", String.class);

        collector.addMethodIfNotOverridden(demo2Method1);
        collector.addMethodIfNotOverridden(demo2Method2);
        collector.addMethodIfNotOverridden(demo2Method3);
        collector.addMethodIfNotOverridden(demo2Method4);
        collector.addMethodIfNotOverridden(demo2Method5);

        collector.addMethodIfNotOverridden(demo1Method1);
        collector.addMethodIfNotOverridden(demo1Method2);
        collector.addMethodIfNotOverridden(demo1Method3);
        collector.addMethodIfNotOverridden(demo1Method4);
        collector.addMethodIfNotOverridden(demo1Method5);

        final List<Method> methods = collector.getMethods();

        final List<Method> expected = Arrays.asList(
                demo2Method1,
                demo2Method2,
                demo2Method3,
                demo2Method4,
                demo2Method5,
                demo1Method4,
                demo1Method5
        );

        assertEquals(expected, methods);
    }

    @Test
    public void addMethodIfNotOverriddenAnotherPackage() throws Exception {
        final MethodCollector collector = new MethodCollector();

        final Method demo1Method1 = OverrideDemo1.class.getDeclaredMethod("method1");
        final Method demo1Method2 = OverrideDemo1.class.getDeclaredMethod("method2");
        final Method demo1Method3 = OverrideDemo1.class.getDeclaredMethod("method3");
        final Method demo1Method4 = OverrideDemo1.class.getDeclaredMethod("method4");

        final Method demo3Method1 = OverrideDemo3.class.getDeclaredMethod("method1");
        final Method demo3Method2 = OverrideDemo3.class.getDeclaredMethod("method2");
        final Method demo3Method3 = OverrideDemo3.class.getDeclaredMethod("method3");
        final Method demo3Method4 = OverrideDemo3.class.getDeclaredMethod("method4");

        collector.addMethodIfNotOverridden(demo3Method1);
        collector.addMethodIfNotOverridden(demo3Method2);
        collector.addMethodIfNotOverridden(demo3Method3);
        collector.addMethodIfNotOverridden(demo3Method4);

        collector.addMethodIfNotOverridden(demo1Method1);
        collector.addMethodIfNotOverridden(demo1Method2);
        collector.addMethodIfNotOverridden(demo1Method3);
        collector.addMethodIfNotOverridden(demo1Method4);

        final List<Method> methods = collector.getMethods();

        final List<Method> expected = Arrays.asList(
                demo3Method1,
                demo3Method2,
                demo3Method3,
                demo3Method4,
                demo1Method3,
                demo1Method4);

        assertEquals(expected, methods);
    }

    @Test
    public void staticMethod() throws Exception {
        final MethodCollector collector = new MethodCollector();
        collector.addMethodIfNotOverridden(Aaa.class.getDeclaredMethod("staticMethod"));

        assertEquals(1, collector.getMethods().size());
    }

    @Test
    public void ignoreBridgeMethod() throws Exception {
        final MethodCollector collector = new MethodCollector();
        for (final Method method : Bbb.class.getDeclaredMethods()) {
            collector.addMethodIfNotOverridden(method);
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
            collector.addMethodIfNotOverridden(method);
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
            collector.addMethodIfNotOverridden(method);
        }
        for (final Method method : Eee.class.getDeclaredMethods()) {
            collector.addMethodIfNotOverridden(method);
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

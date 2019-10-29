package nablarch.fw.dicontainer.annotation.auto;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import nablarch.fw.dicontainer.exception.ClassTraversingException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Runner;
import org.junit.runners.Suite;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;

public class ClassTraverserTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void traverseDirectory() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final ClassTraverser traverser = new ClassTraverser(classLoader,
                Container.class, ClassFilter.allClasses());
        final Set<Class<?>> classes = new HashSet<>();
        traverser.traverse(classes::add);

        assertTrue(classes.contains(AnnotationContainerBuilder.class));
        assertTrue(classes.contains(ClassTraverser.class));
        assertTrue(classes.contains(TraversalConfig.class));
        assertTrue(classes.contains(Container.class));
    }

    @Test
    public void traverseJarFile() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final ClassTraverser traverser = new ClassTraverser(classLoader, Test.class,
                ClassFilter.allClasses());
        final Set<Class<?>> classes = new HashSet<>();
        traverser.traverse(classes::add);

        assertTrue(classes.contains(Test.class));
        assertTrue(classes.contains(Runner.class));
        assertTrue(classes.contains(Suite.class));
        assertTrue(classes.contains(Assert.class));
    }

    /** baseClassが{@link ProtectionDomain#getCodeSource()}でnullを返すとき、処理が中断されること。*/
    @Test
    public void traverseFailsOnSpecifyJavaLangClass() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final ClassTraverser traverser = new ClassTraverser(classLoader,
                String.class, ClassFilter.allClasses());
        traverser.traverse(aClass -> { });
    }

    @Test
    public void traverseDirectoryException() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final ClassTraverser traverser = new ClassTraverser(classLoader,
                Container.class, ClassFilter.allClasses());

        exception.expect(RuntimeException.class);
        traverser.traverse(aClass -> {
            throw new RuntimeException("for test.");
        });
    }


    @Test
    public void handleCheckedExceptionWhenTraversing() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final ClassTraverser traverser = new ClassTraverser(classLoader, getClass(), ClassFilter.allClasses()) {
            @Override
            void traverse(Consumer<Class<?>> consumer, CodeSource codeSource) throws URISyntaxException, IOException {
                throw new IOException("for test.");
            }
        };
        exception.expect(ClassTraversingException.class);
        exception.expectCause(isA(IOException.class));

        traverser.traverse(aClass -> { });

    }

    @Test
    public void handleCheckedExceptionWhenClassLoaded() {
        exception.expect(ClassTraversingException.class);
        exception.expectCause(isA(ClassNotFoundException.class));
        ClassTraverser.forName("invalid class name", getClass().getClassLoader());
    }

}

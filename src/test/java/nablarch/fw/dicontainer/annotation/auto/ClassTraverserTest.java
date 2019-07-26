package nablarch.fw.dicontainer.annotation.auto;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.Suite;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;

public class ClassTraverserTest {

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
}

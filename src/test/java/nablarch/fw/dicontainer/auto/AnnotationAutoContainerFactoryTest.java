package nablarch.fw.dicontainer.auto;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.auto.demo.Auto1;
import nablarch.fw.dicontainer.auto.demo.Auto2;
import nablarch.fw.dicontainer.auto.demo.Auto3;
import nablarch.fw.dicontainer.auto.demo.NotComponent;
import nablarch.fw.dicontainer.auto.demo.subpkg.Auto4;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;

public class AnnotationAutoContainerFactoryTest {

    @Test
    public void create() throws Exception {
        final AnnotationAutoContainerFactory factory = new AnnotationAutoContainerFactory();
        final Iterable<TraversalMark> traversalMarks = Collections.singleton(new TraversalMark() {
            @Override
            public Set<String> includes() {
                return Collections.singleton("^nablarch\\.fw\\.dicontainer\\.auto\\.demo\\..*$$");
            }
        });
        final Container container = factory.create(traversalMarks);

        assertNotNull(container.getComponent(Auto1.class));
        assertNotNull(container.getComponent(Auto2.class));
        assertNotNull(container.getComponent(Auto3.class));
        assertNotNull(container.getComponent(Auto4.class));
        try {
            assertNotNull(container.getComponent(NotComponent.class));
            fail();
        } catch (final ComponentNotFoundException e) {
        }
    }
}
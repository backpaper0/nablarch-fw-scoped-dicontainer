package nablarch.fw.dicontainer.annotation.auto;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.NamedImpl;
import nablarch.fw.dicontainer.annotation.auto.demo.Auto1;
import nablarch.fw.dicontainer.annotation.auto.demo.Auto2;
import nablarch.fw.dicontainer.annotation.auto.demo.Auto3;
import nablarch.fw.dicontainer.annotation.auto.demo.NotComponent;
import nablarch.fw.dicontainer.annotation.auto.demo.subpkg.Auto4;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;

public class AnnotationAutoContainerFactoryTest {

    @Test
    public void create() throws Exception {
        final Iterable<TraversalConfig> traversalConfigs = Collections
                .singleton(new TraversalConfig() {
                    @Override
                    public Set<String> includes() {
                        return Collections.singleton(
                                "^nablarch\\.fw\\.dicontainer\\.annotation\\.auto\\.demo\\..*$$");
                    }
                });
        final AnnotationAutoContainerFactory factory = AnnotationAutoContainerFactory
                .builder()
                .traversalConfigs(traversalConfigs)
                .build();
        final Container container = factory.create();

        assertNotNull(container.getComponent(Auto1.class));
        assertNotNull(container.getComponent(Auto2.class));
        assertNotNull(container.getComponent(Auto3.class, new NamedImpl("")));
        assertNotNull(container.getComponent(Auto4.class));
        try {
            assertNotNull(container.getComponent(NotComponent.class));
            fail();
        } catch (final ComponentNotFoundException e) {
        }
    }
}

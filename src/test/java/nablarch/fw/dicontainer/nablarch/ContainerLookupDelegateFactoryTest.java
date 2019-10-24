package nablarch.fw.dicontainer.nablarch;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import org.junit.Test;

import javax.inject.Singleton;

import static org.junit.Assert.assertNotNull;

public class ContainerLookupDelegateFactoryTest {
    @Test
    public void initialize実行後_コンポーネントを取得できること() {
        AnnotationAutoContainerProvider provider = new AnnotationAutoContainerProvider();
        provider.setAnnotationContainerBuilder(AnnotationContainerBuilder.createDefault()
                .register(Aaa.class));
        provider.initialize();

        ContainerLookupDelegateFactory factory = new ContainerLookupDelegateFactory();
        Object action = factory.create(Aaa.class);
        assertNotNull(action);
    }

    @Singleton
    private static class Aaa {
    }

}
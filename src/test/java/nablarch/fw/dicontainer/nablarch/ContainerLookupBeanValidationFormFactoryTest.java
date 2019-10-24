package nablarch.fw.dicontainer.nablarch;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import org.junit.Test;

import javax.inject.Singleton;

import static org.junit.Assert.*;

public class ContainerLookupBeanValidationFormFactoryTest {


    @Test
    public void initialize実行後_コンポーネントを取得できること() {
        AnnotationAutoContainerProvider provider = new AnnotationAutoContainerProvider();
        provider.setAnnotationContainerBuilder(AnnotationContainerBuilder.createDefault()
                .register(Aaa.class));
        provider.initialize();

        ContainerLookupBeanValidationFormFactory factory = new ContainerLookupBeanValidationFormFactory();
        Aaa form = factory.create(Aaa.class);
        assertNotNull(form);
    }

    @Singleton
    private static class Aaa {
    }

}
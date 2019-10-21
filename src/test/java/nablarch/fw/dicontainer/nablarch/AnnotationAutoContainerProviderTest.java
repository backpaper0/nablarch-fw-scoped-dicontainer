package nablarch.fw.dicontainer.nablarch;

import nablarch.fw.dicontainer.Init;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.auto.TraversalConfig;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.nablarch.RequestComponentTest.Aaa;
import org.junit.Test;

import javax.inject.Singleton;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class AnnotationAutoContainerProviderTest {
    AnnotationAutoContainerProvider sut = new AnnotationAutoContainerProvider();

    @Test
    public void test() {

        sut.setTraversalConfigs(Collections.singletonList(new TraversalConfig() {
        }));
        sut.setComponentPredicate(clazz -> false);
        sut.setEagerLoad(true);
        NablarchWebContextHandler supplier = new NablarchWebContextHandler();
        sut.setRequestContextSupplier(supplier);
        sut.setSessionContextSupplier(supplier);
        sut.initialize();

        ContainerImplementer containerImplementer = ContainerImplementers.get();
        System.out.println("containerImplementer = " + containerImplementer);
    }

    @Test
    public void initialize実行後_コンポーネントを取得できること() {

        sut.setAnnotationContainerBuilder(AnnotationContainerBuilder.createDefault()
                .register(Aaa.class));
        sut.initialize();
        ContainerImplementer containerImplementer = ContainerImplementers.get();
        Aaa component = containerImplementer.getComponent(Aaa.class);
        assertNotNull(component);
    }

    @Test(expected = ContainerCreationException.class)
    public void test2() {
        sut.setAnnotationContainerBuilder(AnnotationContainerBuilder.createDefault()
                .register(InvalidComponent.class));
        sut.initialize();
    }


    @Singleton
    private static class Aaa {
    }

    /** 初期化メソッドが重複した不正なコンポーネント */
    private static class InvalidComponent {
        @Init
        void method1() {
        }

        @Init
        void method2() {
        }
    }

}
package nablarch.fw.dicontainer.component.impl;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ContainerException;
import org.junit.Test;

public class ContainerBuilderWrapperTest {

    @Test(expected = ContainerCreationException.class)
    public void ContainerBuilderに例外がaddされたとき_コンテナの作成時に例外が発生すること() {
        ContainerBuilder<?> containerBuilder = new ContainerBuilder<>();
        ContainerBuilderWrapper sut = new ContainerBuilderWrapper(containerBuilder);
        sut.add(new ContainerException("for test."));
        containerBuilder.build();
    }

    @Test
    public void ContainerBuilderに例外がaddされても除外クラスの場合はコンテナの作成時に例外が発生しないこと() {
        ContainerBuilder<?> containerBuilder = new ContainerBuilder<>();
        ContainerBuilderWrapper sut = new ContainerBuilderWrapper(containerBuilder);
        sut.add(new MyException("for test."));
        sut.ignore(MyException.class);  // 除外クラスに指定
        containerBuilder.build();
    }


    @Test(expected = UnsupportedOperationException.class)
    public void throwExceptionIfExistsError起動時_例外が発生すること() {
        ContainerBuilderWrapper sut = new ContainerBuilderWrapper(new ContainerBuilder<>());
        sut.throwExceptionIfExistsError();
    }

    private static class MyException extends ContainerException {
        private MyException(String s) {
            super(s);
        }
    }
}
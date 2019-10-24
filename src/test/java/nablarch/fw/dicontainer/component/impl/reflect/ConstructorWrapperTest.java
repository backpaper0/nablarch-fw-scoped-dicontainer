package nablarch.fw.dicontainer.component.impl.reflect;

import nablarch.fw.dicontainer.exception.ReflectionException;
import org.junit.Test;

public class ConstructorWrapperTest {

    @Test(expected = ReflectionException.class)
    public void failWhenConstructorThrowsException() throws NoSuchMethodException {
        ConstructorWrapper sut = new ConstructorWrapper(Aaa.class.getDeclaredConstructor());
        sut.newInstance(); // constructor throws Exception
    }

    @Test(expected = ReflectionException.class)
    public void failWhenInstantiationFailed() throws NoSuchMethodException {
        ConstructorWrapper sut = new ConstructorWrapper(Bbb.class.getDeclaredConstructor());
        sut.newInstance();   // abstract class
    }

    private static class Aaa {
        public Aaa() {
            throw new RuntimeException("for test");
        }
    }

    private static abstract class Bbb {
        public Bbb() {
        }
    }

}
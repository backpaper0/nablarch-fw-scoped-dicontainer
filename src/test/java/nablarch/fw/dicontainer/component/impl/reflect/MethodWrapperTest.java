package nablarch.fw.dicontainer.component.impl.reflect;

import nablarch.fw.dicontainer.exception.ReflectionException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MethodWrapperTest {

    @Test(expected = ReflectionException.class)
    public void throwExceptionWhenMethodInvokeFailed() throws NoSuchMethodException {
        MethodWrapper sut = new MethodWrapper(Aaa.class.getDeclaredMethod("doSomething"));
        sut.invoke(new Aaa());
    }

    @Test(expected = ReflectionException.class)
    public void throwExceptionWhenMethodArgumentInvalid() throws NoSuchMethodException {
        MethodWrapper sut = new MethodWrapper(Bbb.class.getDeclaredMethod("doSomething"));
        sut.invoke(new Bbb(), 1);  // invalid argument
    }

    @Test
    public void testGetReturnType() throws NoSuchMethodException {
        MethodWrapper sut = new MethodWrapper(Aaa.class.getDeclaredMethod("doSomething"));
        assertEquals(String.class, sut.getReturnType());
    }

    private static class Aaa {
        public String doSomething() {
            throw new RuntimeException("fot test.");
        }
    }

    private static class Bbb {
        public String doSomething() {
            return "Bbb";
        }
    }
}
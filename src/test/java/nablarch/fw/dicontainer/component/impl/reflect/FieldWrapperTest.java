package nablarch.fw.dicontainer.component.impl.reflect;

import nablarch.fw.dicontainer.exception.ReflectionException;
import org.junit.Test;

import static org.junit.Assert.*;

public class FieldWrapperTest {

    @Test(expected = ReflectionException.class)
    public void throwsExceptionWhenFieldAccessFailed() throws NoSuchFieldException {
        FieldWrapper sut = new FieldWrapper(Aaa.class.getDeclaredField("str"));
        sut.set(new Aaa(), 1);
    }

    private static class Aaa {
        private String str;
    }
}
package nablarch.fw.dicontainer.exception;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClassTraversingExceptionTest {

    @Test
    public void testConstructor() {
        ClassTraversingException sut = new ClassTraversingException(new IOException("for test."));
    }

}
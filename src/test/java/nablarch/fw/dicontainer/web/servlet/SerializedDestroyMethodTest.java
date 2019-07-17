package nablarch.fw.dicontainer.web.servlet;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import nablarch.fw.dicontainer.web.servlet.SerializedDestroyMethod;
import nablarch.fw.dicontainer.web.servlet.SerializedDestroyMethod.SerializedDestroyMethodImpl;

public class SerializedDestroyMethodTest {

    @Test
    public void serialize() throws Exception {
        final Class<?> declaringClass = SerializedDestroyMethodTest.class;
        final String methodName = "serialize";
        final SerializedDestroyMethod method1 = new SerializedDestroyMethodImpl(declaringClass,
                methodName);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(method1);
        }

        final SerializedDestroyMethod method2;
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            method2 = (SerializedDestroyMethod) ois.readObject();
        }

        assertNotNull(method2);
    }
}

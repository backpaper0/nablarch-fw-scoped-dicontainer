package nablarch.fw.dicontainer.nablarch;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ContainersTest {

    /** カバレッジ用のテスト。 */
    @Test
    public void testConstructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Containers> constructor = Containers.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();

    }
}
package nablarch.fw.dicontainer.component.impl;

import org.junit.Test;

public class NoopDestroyMethodTest {

    @Test
    public void 起動しても何も起こらないこと() {
        NoopDestroyMethod sut = new NoopDestroyMethod();
        Object arg = new Object();
        sut.invoke(arg);
    }
}
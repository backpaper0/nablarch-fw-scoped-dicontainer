package nablarch.fw.dicontainer.component.impl;

import org.junit.Test;

public class PassthroughInjectableConstructorTest {

    /**
     * 格納したインスタンスをそのまま返却する{@link nablarch.fw.dicontainer.component.InjectableConstructor}実装クラスであるため、
     * {@link PassthroughInjectableConstructor#validateCycleDependency}を起動しても何も起こらないこと。
     */
    @Test
    public void testValidateCycleDependencyNop() {
        PassthroughInjectableConstructor sut = new PassthroughInjectableConstructor(new Object());
        sut.validateCycleDependency(null);
    }
}
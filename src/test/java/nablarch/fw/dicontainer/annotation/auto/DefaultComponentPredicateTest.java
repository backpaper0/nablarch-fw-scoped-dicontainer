package nablarch.fw.dicontainer.annotation.auto;

import nablarch.fw.dicontainer.web.RequestScoped;
import org.junit.Test;

import javax.inject.Named;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultComponentPredicateTest {

    private DefaultComponentPredicate sut = new DefaultComponentPredicate();

    /** 特定のアノテーションが付与されたクラスが、コンポーネントであると判定されること。*/
    @Test
    public void test() {
        assertTrue(sut.test(Aaa.class));
        assertTrue(sut.test(Bbb.class));
    }

    /** 特定のアノテーションが付与されていないクラスは、コンポーネントと判定されないこと。*/
    @Test
    public void test2() {
        assertFalse(sut.test(Ccc.class));
        assertFalse(sut.test(Ddd.class));
    }

    /** {@link javax.inject.Scope}がある */
    @RequestScoped
    private static class Aaa {
        private String aaa;
    }

    /** {@link javax.inject.Qualifier}がある*/
    @Named("bbb")
    private static class Bbb {
    }

    /** アノテーションがない */
    private static class Ccc {
    }

    /** 関係ないアノテーションがある */
    @Deprecated
    private static class Ddd {
    }
}
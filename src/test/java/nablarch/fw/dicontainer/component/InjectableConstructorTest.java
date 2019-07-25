package nablarch.fw.dicontainer.component;

import static org.junit.Assert.*;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

import nablarch.fw.dicontainer.NamedImpl;
import nablarch.fw.dicontainer.TinyContainer;
import nablarch.fw.dicontainer.annotation.AnnotationMemberFactory;
import nablarch.fw.dicontainer.component.factory.MemberFactory;

public class InjectableConstructorTest {

    private final MemberFactory factory = AnnotationMemberFactory.createDefault();
    private final ErrorCollector errorCollector = ErrorCollector.newInstance();

    @Test
    public void fromClass() throws Exception {
        final InjectableConstructor creator = factory.createConstructor(Aaa.class, errorCollector)
                .get();
        final TinyContainer container = new TinyContainer();
        final Aaa component = (Aaa) creator.inject(container);
        assertTrue(component.called);
    }

    @Test
    public void fromClassWithInjection() throws Exception {
        final InjectableConstructor creator = factory.createConstructor(Bbb.class, errorCollector)
                .get();
        final TinyContainer container = new TinyContainer(Aaa.class);
        final Bbb component = (Bbb) creator.inject(container);
        assertTrue(component.called);
    }

    @Test
    public void fromClassWithQualifierParameter() throws Exception {
        final InjectableConstructor creator = factory.createConstructor(Ddd.class, errorCollector)
                .get();
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        final TinyContainer container = new TinyContainer()
                .register(
                        new ComponentKey<>(Ccc.class, new NamedImpl("first")),
                        new Ccc(uuid1))
                .register(
                        new ComponentKey<>(Ccc.class, new NamedImpl("second")),
                        new Ccc(uuid2));
        final Ddd component = (Ddd) creator.inject(container);
        assertEquals(uuid1, component.ccc1.value);
        assertEquals(uuid2, component.ccc2.value);
    }

    static class Aaa {

        boolean called;

        Aaa() {
            called = true;
        }
    }

    static class Bbb {

        boolean called;

        Bbb() {
        }

        @Inject
        Bbb(final Aaa aaa) {
            called = true;
        }
    }

    static class Ccc {

        final UUID value;

        Ccc(final UUID value) {
            this.value = value;
        }
    }

    static class Ddd {

        final Ccc ccc1;
        final Ccc ccc2;

        @Inject
        Ddd(@Named("first") final Ccc ccc1, @Named("second") final Ccc ccc2) {
            this.ccc1 = ccc1;
            this.ccc2 = ccc2;
        }
    }
}

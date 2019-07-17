package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

public class InjectableConstructorTest {

    private final AnnotationMemberFactory factory = new AnnotationMemberFactory();

    @Test
    public void fromClass() throws Exception {
        final InjectableMember creator = factory.createConstructor(Aaa.class, new ErrorCollector());
        final Container container = new TinyContainer();
        final Aaa component = (Aaa) creator.inject(container, null);
        assertTrue(component.called);
    }

    @Test
    public void fromClassWithInjection() throws Exception {
        final InjectableMember creator = factory.createConstructor(Bbb.class, new ErrorCollector());
        final Container container = new TinyContainer(Aaa.class);
        final Bbb component = (Bbb) creator.inject(container, null);
        assertTrue(component.called);
    }

    @Test
    public void fromClassWithQualifierParameter() throws Exception {
        final InjectableMember creator = factory.createConstructor(Ddd.class, new ErrorCollector());
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        final Container container = new TinyContainer()
                .register(
                        new ComponentKey<>(Ccc.class,
                                Collections.singleton(
                                        Qualifier.fromAnnotation(new NamedImpl("first")))),
                        new Ccc(uuid1))
                .register(
                        new ComponentKey<>(Ccc.class,
                                Collections.singleton(
                                        Qualifier.fromAnnotation(new NamedImpl("second")))),
                        new Ccc(uuid2));
        final Ddd component = (Ddd) creator.inject(container, null);
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

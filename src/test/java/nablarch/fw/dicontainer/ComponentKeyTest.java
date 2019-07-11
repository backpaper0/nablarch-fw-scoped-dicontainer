package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Named;

import org.junit.Test;

import nablarch.fw.dicontainer.ComponentKey.AliasKey;

public class ComponentKeyTest {

    @Test
    public void aliasKeysEmpty() throws Exception {
        final ComponentKey<Aaa> key = ComponentKey.fromClass(Aaa.class);
        final Set<AliasKey> aliasKeys = key.aliasKeys();
        assertTrue(aliasKeys.isEmpty());
    }

    @Test
    public void aliasKeys() throws Exception {
        final ComponentKey<Bbb4> key = ComponentKey.fromClass(Bbb4.class);
        final Set<AliasKey> aliasKeys = key.aliasKeys();
        final Set<AliasKey> expected = Stream.of(
                new ComponentKey.AliasKey(Bbb1.class, Collections.emptySet()),
                new ComponentKey.AliasKey(Bbb2.class, Collections.emptySet()),
                new ComponentKey.AliasKey(Bbb3.class, Collections.emptySet()))
                .collect(Collectors.toSet());
        assertEquals(expected, aliasKeys);
    }

    @Test
    public void qualifier() throws Exception {
        final ComponentKey<Ccc2> key = ComponentKey.fromClass(Ccc2.class);
        final ComponentKey<Ccc2> expected = new ComponentKey<>(Ccc2.class,
                Collections.singleton(NamedImpl.qualifier("foo")));
        assertEquals(expected, key);
    }

    @Test
    public void aliasViaQualifier() throws Exception {
        final Set<AliasKey> aliasKeys = ComponentKey.fromClass(Ccc2.class).aliasKeys();
        final AliasKey aliasKey = new AliasKey(Ccc1.class, Collections.singleton(NamedImpl.qualifier("foo")));
        final Set<AliasKey> expected = Collections.singleton(aliasKey);
        assertEquals(expected, aliasKeys);
    }

    static class Aaa {
    }

    interface Bbb1 {
    }

    interface Bbb2 extends Bbb1 {
    }

    static class Bbb3 implements Bbb2 {
    }

    static class Bbb4 extends Bbb3 {
    }

    interface Ccc1 {
    }

    @Named("foo")
    static class Ccc2 implements Ccc1 {
    }
}

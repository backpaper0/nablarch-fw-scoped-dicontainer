package nablarch.fw.dicontainer.component;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import nablarch.fw.dicontainer.component.ComponentKey.AliasKey;

public class AliasMappingTest {

    private final AliasKey aliasKey1 = new ComponentKey<>(Object.class).asAliasKey();
    private final AliasKey aliasKey2 = new ComponentKey<>(Double.class).asAliasKey();
    private final ComponentKey<String> key1 = new ComponentKey<>(String.class);
    private final ComponentKey<Integer> key2 = new ComponentKey<>(Integer.class);
    private final ComponentKey<Boolean> key3 = new ComponentKey<>(Boolean.class);

    private final AliasMapping mapping = new AliasMapping();

    @Before
    public void setUp() throws Exception {
        mapping.register(aliasKey1, key1);
        mapping.register(aliasKey1, key2);
        mapping.register(aliasKey1, key3);
    }

    @Test
    public void find() throws Exception {
        final Set<ComponentKey<?>> keys = mapping.find(aliasKey1);
        final Set<ComponentKey<?>> expected = Stream.of(key1, key2, key3)
                .collect(Collectors.toSet());
        assertEquals(expected, keys);
    }

    @Test
    public void notFound() throws Exception {
        final Set<ComponentKey<?>> keys = mapping.find(aliasKey2);
        assertTrue(keys.isEmpty());
    }
}

package nablarch.fw.dicontainer.annotation.auto;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.auto.ClassFilter;
import nablarch.fw.dicontainer.annotation.auto.TraversalConfig;

public class ClassFilterTest {

    @Test
    public void nothing() throws Exception {
        final TraversalConfig traversalMark = new TraversalConfig() {
        };
        final ClassFilter classFilter = ClassFilter.valueOf(traversalMark);

        assertTrue(classFilter.select("aaa.bbb.Ccc"));
        assertTrue(classFilter.select("aaa.bbb.Ddd"));
    }

    @Test
    public void includes() throws Exception {
        final TraversalConfig traversalMark = new TraversalConfig() {
            @Override
            public Set<String> includes() {
                return Collections.singleton("^.*\\.Ccc$");
            }
        };
        final ClassFilter classFilter = ClassFilter.valueOf(traversalMark);

        assertTrue(classFilter.select("aaa.bbb.Ccc"));
        assertFalse(classFilter.select("aaa.bbb.Ddd"));
    }

    @Test
    public void excludes() throws Exception {
        final TraversalConfig traversalMark = new TraversalConfig() {
            @Override
            public Set<String> excludes() {
                return Collections.singleton("^.*\\.Ccc$");
            }
        };
        final ClassFilter classFilter = ClassFilter.valueOf(traversalMark);

        assertFalse(classFilter.select("aaa.bbb.Ccc"));
        assertTrue(classFilter.select("aaa.bbb.Ddd"));
    }

    @Test
    public void both() throws Exception {
        final TraversalConfig traversalMark = new TraversalConfig() {
            @Override
            public Set<String> includes() {
                return Collections.singleton("^aaa\\.bbb\\..*$");
            }

            @Override
            public Set<String> excludes() {
                return Collections.singleton("^.*\\.Ddd$");
            }
        };
        final ClassFilter classFilter = ClassFilter.valueOf(traversalMark);

        assertTrue(classFilter.select("aaa.bbb.Ccc"));
        assertFalse(classFilter.select("aaa.bbb.Ddd"));
        assertTrue(classFilter.select("aaa.bbb.Eee"));
        assertFalse(classFilter.select("xxx.yyy.Ccc"));
    }

    @Test
    public void both2() throws Exception {
        final TraversalConfig traversalMark = new TraversalConfig() {
            @Override
            public Set<String> includes() {
                return Stream.of(
                        "^aaa\\.bbb\\..*$",
                        "^xxx\\.yyy\\..*$").collect(Collectors.toSet());
            }

            @Override
            public Set<String> excludes() {
                return Stream.of(
                        "^.*\\.ccc\\..*$",
                        "^.*\\.zzz\\..*$").collect(Collectors.toSet());
            }
        };
        final ClassFilter classFilter = ClassFilter.valueOf(traversalMark);

        assertTrue(classFilter.select("aaa.bbb.rrr.Sss"));
        assertTrue(classFilter.select("xxx.yyy.rrr.Sss"));
        assertFalse(classFilter.select("ppp.qqq.rrr.Sss"));

        assertFalse(classFilter.select("aaa.bbb.ccc.Sss"));
        assertFalse(classFilter.select("xxx.yyy.zzz.Sss"));
    }
}

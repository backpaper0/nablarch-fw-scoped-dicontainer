package nablarch.fw.dicontainer.scope;

import nablarch.fw.dicontainer.nablarch.NablarchWebContextHandler;
import nablarch.fw.dicontainer.web.scope.RequestScope;
import nablarch.fw.dicontainer.web.scope.SessionScope;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class ScopeTest {

    /** {@link Scope#dimensions()}の順が Prototype < Request < Session < Singleton であること。 */
    @Test
    public void testScopeDimensions() {
        NablarchWebContextHandler supplier = new NablarchWebContextHandler();
        Scope prototype = new PrototypeScope();
        Scope request = new RequestScope(supplier);
        Scope session = new SessionScope(supplier);
        Scope singleton = new SingletonScope();

        List<Scope> scopes = Arrays.asList(prototype, request, session, singleton);
        scopes.sort(Comparator.comparingInt(Scope::dimensions));

        assertThat(scopes.get(0), sameInstance(prototype));
        assertThat(scopes.get(1), sameInstance(request));
        assertThat(scopes.get(2), sameInstance(session));
        assertThat(scopes.get(3), sameInstance(singleton));

    }
}
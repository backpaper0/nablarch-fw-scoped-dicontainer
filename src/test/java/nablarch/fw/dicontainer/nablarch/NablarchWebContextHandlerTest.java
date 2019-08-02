package nablarch.fw.dicontainer.nablarch;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import nablarch.fw.ExecutionContext;
import nablarch.fw.dicontainer.nablarch.NablarchWebContextHandler;
import nablarch.fw.dicontainer.web.exception.WebContextException;
import nablarch.fw.dicontainer.web.servlet.MockServletRequests;

public class NablarchWebContextHandlerTest {

    @Test
    public void contextMustNotBeNested() throws Exception {
        final NablarchWebContextHandler supplier = new NablarchWebContextHandler();

        final HttpServletRequest request = MockServletRequests.createMock();
        try {
            supplier.handle(null, new ExecutionContext().addHandler((data, context) -> {
                supplier.handle(null, new ExecutionContext().addHandler((data2, context2) -> {
                    return null;
                }));
                return null;
            }));
            fail();
        } catch (final WebContextException e) {
        }
    }
}

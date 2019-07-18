package nablarch.fw.dicontainer.web.servlet;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import nablarch.fw.dicontainer.exception.web.WebContextException;

public class ServletAPIContextSupplierTest {

    @Test
    public void contextMustNotBeNested() throws Exception {
        final ServletAPIContextSupplier supplier = new ServletAPIContextSupplier();

        final HttpServletRequest request = MockServletRequests.createMock();
        try {
            supplier.doWithContext(request, () -> {
                supplier.doWithContext(request, () -> {
                });
            });
            fail();
        } catch (final WebContextException e) {
        }
    }
}
